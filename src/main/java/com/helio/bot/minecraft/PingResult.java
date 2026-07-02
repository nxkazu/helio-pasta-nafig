package com.helio.bot.minecraft;

/** Результат пинга Minecraft-сервера (Java или Bedrock). */
public class PingResult {
    public String host;
    public int port;
    public String edition;
    public String versionName;
    public int online;
    public int max;
    public String motd;
    public long pingMs;
}
