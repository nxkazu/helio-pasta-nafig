package com.helio.bot.commands.text;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashCommand implements Command {

    @Override
    public String name() {
        return "hash";
    }

    @Override
    public String description() {
        return "Хеширование текста.";
    }

    @Override
    public Category category() {
        return Category.TEXT;
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        String arg = ctx.argsTrimmed();
        if (arg.isBlank()) {
            ctx.reply("Использование: /hash <текст>");
            return;
        }
        ctx.reply("MD5: " + digest("MD5", arg) + "\n"
                + "SHA-1: " + digest("SHA-1", arg) + "\n"
                + "SHA-256: " + digest("SHA-256", arg));
    }

    private static String digest(String algo, String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algo);
        byte[] d = md.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : d) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
