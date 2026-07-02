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
public class WeatherCommand implements Command {

    private final HttpService http;

    @Override
    public String name() {
        return "weather";
    }

    @Override
    public String description() {
        return "Погода в указанной точке.";
    }

    @Override
    public Category category() {
        return Category.NETWORK;
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        String place = ctx.argsTrimmed();
        if (place.isBlank()) {
            ctx.reply("Использование: /weather <город>");
            return;
        }
        String geo = http.get("https://geocoding-api.open-meteo.com/v1/search?count=1&language=ru&name="
                + URLEncoder.encode(place, StandardCharsets.UTF_8));
        JsonObject g = JsonParser.parseString(geo).getAsJsonObject();
        if (!g.has("results") || g.getAsJsonArray("results").isEmpty()) {
            ctx.reply("Место не найдено.");
            return;
        }
        JsonObject loc = g.getAsJsonArray("results").get(0).getAsJsonObject();
        double lat = loc.get("latitude").getAsDouble();
        double lon = loc.get("longitude").getAsDouble();
        String name = loc.get("name").getAsString()
                + (loc.has("country") ? ", " + loc.get("country").getAsString() : "");
        String wx = http.get("https://api.open-meteo.com/v1/forecast?current=temperature_2m,"
                + "relative_humidity_2m,wind_speed_10m&latitude=" + lat + "&longitude=" + lon);
        JsonObject w = JsonParser.parseString(wx).getAsJsonObject().getAsJsonObject("current");
        ctx.reply("🌤 Погода: " + name + "\n"
                + "Температура: " + w.get("temperature_2m").getAsDouble() + "°C\n"
                + "Влажность: " + w.get("relative_humidity_2m").getAsInt() + "%\n"
                + "Ветер: " + w.get("wind_speed_10m").getAsDouble() + " км/ч");
    }
}
