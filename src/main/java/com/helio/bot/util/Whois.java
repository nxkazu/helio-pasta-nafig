package com.helio.bot.util;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** WHOIS-клиент поверх TCP/43 с поддержкой referral (IANA → профильный сервер). */
public final class Whois {
    private Whois() {
    }

    public static String lookup(String query) throws Exception {
        String root = query(query, "whois.iana.org");
        String refer = null;
        for (String line : root.split("\n")) {
            String l = line.trim().toLowerCase();
            if (l.startsWith("refer:") || l.startsWith("whois:")) {
                String raw = line.trim();
                refer = raw.substring(raw.indexOf(':') + 1).trim();
                break;
            }
        }
        if (refer != null && !refer.isBlank()) {
            try {
                return query(query, refer);
            } catch (Exception ignored) {
            }
        }
        return root;
    }

    private static String query(String query, String server) throws Exception {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(server, 43), 8000);
            s.setSoTimeout(8000);
            s.getOutputStream().write((query + "\r\n").getBytes(StandardCharsets.UTF_8));
            s.getOutputStream().flush();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = s.getInputStream().read(buf)) != -1) {
                bos.write(buf, 0, n);
            }
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
