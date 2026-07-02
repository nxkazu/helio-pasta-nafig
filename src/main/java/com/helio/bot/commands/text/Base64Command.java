package com.helio.bot.commands.text;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Command implements Command {

    @Override
    public String name() {
        return "base64";
    }

    @Override
    public String description() {
        return "Конвертация текста в base64 и обратно.";
    }

    @Override
    public Category category() {
        return Category.TEXT;
    }

    @Override
    public java.util.List<String> aliases() {
        return java.util.List.of("b64");
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        String arg = ctx.argsTrimmed();
        if (arg.isBlank()) {
            ctx.reply("Использование: /base64 <текст>  |  /base64 -d <base64>");
            return;
        }
        if (arg.startsWith("-d ")) {
            try {
                ctx.reply(new String(Base64.getDecoder().decode(arg.substring(3).trim()), StandardCharsets.UTF_8));
            } catch (IllegalArgumentException e) {
                ctx.reply("Некорректный base64.");
            }
        } else {
            ctx.reply(Base64.getEncoder().encodeToString(arg.getBytes(StandardCharsets.UTF_8)));
        }
    }
}
