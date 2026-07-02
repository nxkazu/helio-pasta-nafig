package com.helio.bot.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** Реализация Server List Ping для Minecraft Java Edition (протокол рукопожатия + status). */
public final class JavaPinger {
    private JavaPinger() {
    }

    public static PingResult ping(String host, int port, int timeoutMs) throws IOException {
        long start = System.currentTimeMillis();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            socket.setSoTimeout(timeoutMs);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            ByteArrayOutputStream handshake = new ByteArrayOutputStream();
            DataOutputStream hs = new DataOutputStream(handshake);
            writeVarInt(hs, 0x00);
            writeVarInt(hs, 47);
            writeString(hs, host);
            hs.writeShort(port);
            writeVarInt(hs, 1);
            writePacket(out, handshake.toByteArray());

            ByteArrayOutputStream statusReq = new ByteArrayOutputStream();
            writeVarInt(new DataOutputStream(statusReq), 0x00);
            writePacket(out, statusReq.toByteArray());

            readVarInt(in);
            int packetId = readVarInt(in);
            if (packetId != 0x00) {
                throw new IOException("Неожиданный packet id: " + packetId);
            }
            int jsonLen = readVarInt(in);
            byte[] jsonBytes = new byte[jsonLen];
            in.readFully(jsonBytes);
            JsonObject root = JsonParser.parseString(new String(jsonBytes, StandardCharsets.UTF_8)).getAsJsonObject();

            PingResult r = new PingResult();
            r.pingMs = System.currentTimeMillis() - start;
            if (root.has("players")) {
                JsonObject players = root.getAsJsonObject("players");
                r.online = players.get("online").getAsInt();
                r.max = players.get("max").getAsInt();
            }
            if (root.has("version")) {
                r.versionName = root.getAsJsonObject("version").get("name").getAsString();
            }
            if (root.has("description")) {
                r.motd = descriptionToString(root.get("description"));
            }
            return r;
        }
    }

    private static String descriptionToString(JsonElement desc) {
        if (desc.isJsonPrimitive()) {
            return desc.getAsString();
        }
        if (desc.isJsonObject()) {
            JsonObject o = desc.getAsJsonObject();
            StringBuilder sb = new StringBuilder();
            if (o.has("text")) {
                sb.append(o.get("text").getAsString());
            }
            if (o.has("extra")) {
                for (JsonElement e : o.getAsJsonArray("extra")) {
                    sb.append(descriptionToString(e));
                }
            }
            return sb.toString();
        }
        return desc.toString();
    }

    private static void writePacket(DataOutputStream out, byte[] data) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(buf);
        writeVarInt(d, data.length);
        d.write(data);
        out.write(buf.toByteArray());
        out.flush();
    }

    private static void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & -128) != 0) {
            out.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    private static void writeString(DataOutputStream out, String s) throws IOException {
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        writeVarInt(out, b.length);
        out.write(b);
    }

    private static int readVarInt(DataInputStream in) throws IOException {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = in.readByte();
            result |= ((read & 127) << (7 * numRead));
            numRead++;
            if (numRead > 5) {
                throw new IOException("VarInt слишком большой");
            }
        } while ((read & 128) == 128);
        return result;
    }
}
