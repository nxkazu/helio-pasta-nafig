package com.helio.bot.commands.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.util.HttpService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LauncherAdCommand implements Command {

    private final HttpService http;

    @Override
    public String name() {
        return "launcherad";
    }

    @Override
    public String description() {
        return "Список рекламируемых серверов в TL/LL.";
    }

    @Override
    public Category category() {
        return Category.MINECRAFT;
    }

    @Override
    public void execute(CommandContext ctx) {
        String url = System.getenv("LAUNCHER_AD_URL");
        if (url == null || url.isBlank()) {
            ctx.reply("📢 Источник рекламируемых серверов TL/LL не настроен.\n"
                    + "Задай переменную окружения LAUNCHER_AD_URL (JSON-список вида "
                    + "[{\"name\":\"...\",\"ip\":\"...\"}]), и команда начнёт выводить список.");
            return;
        }
        try {
            JsonElement root = JsonParser.parseString(http.get(url));
            JsonArray arr;
            if (root.isJsonArray()) {
                arr = root.getAsJsonArray();
            } else if (root.isJsonObject() && root.getAsJsonObject().has("servers")) {
                arr = root.getAsJsonObject().getAsJsonArray("servers");
            } else {
                arr = new JsonArray();
            }
            if (arr.isEmpty()) {
                ctx.reply("📢 Список рекламируемых серверов пуст.");
                return;
            }
            StringBuilder sb = new StringBuilder("📢 Рекламируемые серверы (TL/LL):\n");
            int i = 1;
            for (JsonElement e : arr) {
                if (!e.isJsonObject()) {
                    continue;
                }
                JsonObject o = e.getAsJsonObject();
                String name = o.has("name") ? o.get("name").getAsString() : "?";
                String ip = o.has("ip") ? o.get("ip").getAsString()
                        : (o.has("address") ? o.get("address").getAsString() : "?");
                sb.append(i++).append(". ").append(name).append(" — ").append(ip).append("\n");
                if (i > 50) {
                    break;
                }
            }
            ctx.reply(sb.toString());
        } catch (Exception e) {
            ctx.reply("Не удалось получить список: " + e.getMessage());
        }
    }
}
