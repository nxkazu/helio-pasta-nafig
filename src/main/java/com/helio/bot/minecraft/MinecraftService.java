package com.helio.bot.minecraft;

/** Доменный сервис пинга: сначала пробует Java Edition, затем Bedrock; проставляет edition/host/port. */
public class MinecraftService {

    private static final int TIMEOUT = 5000;

    public PingResult ping(String input) throws Exception {
        HostPort hp = HostPort.parse(input, -1);
        try {
            int port = hp.port() < 0 ? 25565 : hp.port();
            PingResult r = JavaPinger.ping(hp.host(), port, TIMEOUT);
            r.host = hp.host();
            r.port = port;
            r.edition = "Java";
            return r;
        } catch (Exception ignored) {
            // fallthrough to Bedrock
        }
        int port = hp.port() < 0 ? 19132 : hp.port();
        PingResult r = BedrockPinger.ping(hp.host(), port, TIMEOUT);
        r.host = hp.host();
        r.port = port;
        r.edition = "Bedrock";
        return r;
    }
}
