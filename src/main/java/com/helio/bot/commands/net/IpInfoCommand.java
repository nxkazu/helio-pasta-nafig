package com.helio.bot.commands.net;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.util.HttpService;
import lombok.RequiredArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class IpInfoCommand implements Command {

    private final HttpService http;

    @Override
    public String name() {
        return "ipinfo";
    }

    @Override
    public String description() {
        return "Информация об IP.";
    }

    @Override
    public Category category() {
        return Category.NETWORK;
    }

    private static String get(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "-";
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        String ip = ctx.argsTrimmed();
        if (ip.isBlank()) {
            ctx.reply("Использование: /ipinfo <IP>");
            return;
        }
        String body = http.get("http://ip-api.com/json/" + URLEncoder.encode(ip, StandardCharsets.UTF_8)
                + "?fields=status,message,country,regionName,city,isp,org,as,query,timezone");
        JsonObject o = JsonParser.parseString(body).getAsJsonObject();
        if (!"success".equals(get(o, "status"))) {
            ctx.reply("Не удалось получить данные: " + get(o, "message"));
            return;
        }
        ctx.reply("🌐 " + get(o, "query") + "\n"
                + "Страна: " + get(o, "country") + "\n"
                + "Регион: " + get(o, "regionName") + "\n"
                + "Город: " + get(o, "city") + "\n"
                + "ISP: " + get(o, "isp") + "\n"
                + "Орг: " + get(o, "org") + "\n"
                + "AS: " + get(o, "as") + "\n"
                + "TZ: " + get(o, "timezone"));
    }
}
