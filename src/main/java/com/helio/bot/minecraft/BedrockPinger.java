package com.helio.bot.minecraft;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/** RakNet unconnected ping для Minecraft Bedrock Edition. */
public final class BedrockPinger {
    private BedrockPinger() {
    }

    private static final byte[] MAGIC = {
            0x00, (byte) 0xFF, (byte) 0xFF, 0x00, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE,
            (byte) 0xFD, (byte) 0xFD, (byte) 0xFD, (byte) 0xFD, 0x12, 0x34, 0x56, 0x78
    };

    public static PingResult ping(String host, int port, int timeoutMs) throws Exception {
        long start = System.currentTimeMillis();
        try (DatagramSocket sock = new DatagramSocket()) {
            sock.setSoTimeout(timeoutMs);
            ByteBuffer buf = ByteBuffer.allocate(1 + 8 + 16 + 8);
            buf.put((byte) 0x01);
            buf.putLong(System.currentTimeMillis());
            buf.put(MAGIC);
            buf.putLong(0x1234567890ABCDEFL);
            byte[] out = buf.array();

            sock.send(new DatagramPacket(out, out.length, InetAddress.getByName(host), port));

            byte[] in = new byte[4096];
            DatagramPacket resp = new DatagramPacket(in, in.length);
            sock.receive(resp);

            ByteBuffer rb = ByteBuffer.wrap(in, 0, resp.getLength());
            rb.get();       // id 0x1C
            rb.getLong();   // ping time
            rb.getLong();   // server GUID
            byte[] magic = new byte[16];
            rb.get(magic);
            int strLen = rb.getShort() & 0xFFFF;
            byte[] strBytes = new byte[Math.min(strLen, rb.remaining())];
            rb.get(strBytes);
            String[] f = new String(strBytes, StandardCharsets.UTF_8).split(";", -1);

            PingResult r = new PingResult();
            r.pingMs = System.currentTimeMillis() - start;
            if (f.length > 1) {
                r.motd = f[1];
            }
            if (f.length > 3) {
                r.versionName = f[3];
            }
            if (f.length > 4) {
                r.online = parseInt(f[4]);
            }
            if (f.length > 5) {
                r.max = parseInt(f[5]);
            }
            return r;
        }
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
