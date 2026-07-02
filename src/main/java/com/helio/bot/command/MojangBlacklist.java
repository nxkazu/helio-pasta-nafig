package com.helio.bot.command;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Проверка нахождения сервера в чёрном списке Mojang (blockedservers, SHA-1 + wildcard). */
public final class MojangBlacklist {
    private MojangBlacklist() {
    }

    public static boolean isBlocked(String host) throws Exception {
        String body = Utils.httpGet("https://sessionserver.mojang.com/blockedservers");
        Set<String> blocked = new HashSet<>();
        for (String line : body.split("\\R")) {
            String t = line.trim();
            if (!t.isEmpty()) {
                blocked.add(t);
            }
        }
        for (String v : variants(host.toLowerCase())) {
            if (blocked.contains(sha1(v))) {
                return true;
            }
        }
        return false;
    }

    private static List<String> variants(String host) {
        List<String> out = new ArrayList<>();
        out.add(host);
        if (host.matches("\\d{1,3}(\\.\\d{1,3}){3}")) {
            String[] o = host.split("\\.");
            // 1.2.3.4 -> 1.2.3.* -> 1.2.* -> 1.*
            for (int i = o.length - 1; i >= 1; i--) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < i; j++) {
                    if (j > 0) {
                        sb.append('.');
                    }
                    sb.append(o[j]);
                }
                sb.append(".*");
                out.add(sb.toString());
            }
        } else {
            String[] labels = host.split("\\.");
            // mc.example.com -> *.example.com -> *.com
            for (int i = 1; i < labels.length; i++) {
                StringBuilder sb = new StringBuilder("*");
                for (int j = i; j < labels.length; j++) {
                    sb.append('.').append(labels[j]);
                }
                out.add(sb.toString());
            }
        }
        return out;
    }

    private static String sha1(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : d) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
