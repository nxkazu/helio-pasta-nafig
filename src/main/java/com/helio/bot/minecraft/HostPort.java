package com.helio.bot.minecraft;

/** Разбор строки "host" или "host:port" с портом по умолчанию (-1 = не задан). */
public record HostPort(String host, int port) {
    public static HostPort parse(String input, int defaultPort) {
        String host = input;
        int port = defaultPort;
        if (input.contains(":")) {
            String[] p = input.split(":", 2);
            host = p[0];
            try {
                port = Integer.parseInt(p[1].trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return new HostPort(host, port);
    }
}
