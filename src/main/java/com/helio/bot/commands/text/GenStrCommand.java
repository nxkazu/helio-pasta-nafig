package com.helio.bot.commands.text;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import java.security.SecureRandom;

public class GenStrCommand implements Command {

    @Override
    public String name() {
        return "genstr";
    }

    @Override
    public String description() {
        return "Генерация строки.";
    }

    @Override
    public Category category() {
        return Category.TEXT;
    }

    @Override
    public java.util.List<String> aliases() {
        return java.util.List.of("rand");
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        String[] a = ctx.argList();
        int len = 16;
        if (a.length > 0) {
            try {
                len = Integer.parseInt(a[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        len = Math.max(1, Math.min(len, 4096));
        String charset = (a.length > 1 && a[1].equalsIgnoreCase("hex"))
                ? "0123456789abcdef"
                : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(charset.charAt(rnd.nextInt(charset.length())));
        }
        ctx.reply(sb.toString());
    }
}
