package com.helio.bot.commands.net;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.util.HttpService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CurrencyCommand implements Command {

    private final HttpService http;

    @Override
    public String name() {
        return "currency";
    }

    @Override
    public String description() {
        return "Курс валют относительно рубля (ЦБР).";
    }

    @Override
    public Category category() {
        return Category.NETWORK;
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        String body = http.get("https://www.cbr-xml-daily.ru/daily_json.js");
        JsonObject valute = JsonParser.parseString(body).getAsJsonObject().getAsJsonObject("Valute");
        String arg = ctx.argsTrimmed().toUpperCase();
        StringBuilder sb = new StringBuilder("💱 Курс ЦБ РФ:\n");
        String[] codes = arg.isBlank() ? new String[]{"USD", "EUR", "CNY", "GBP", "JPY"} : arg.split("\\s+");
        for (String code : codes) {
            if (valute.has(code)) {
                JsonObject c = valute.getAsJsonObject(code);
                sb.append(c.get("Nominal").getAsInt()).append(" ").append(code)
                        .append(" = ").append(String.format("%.4f", c.get("Value").getAsDouble())).append(" ₽\n");
            } else {
                sb.append(code).append(": не найдено\n");
            }
        }
        if (arg.isBlank()) {
            sb.append("\nПодсказка: /currency USD EUR");
        }
        ctx.reply(sb.toString());
    }
}
