package com.helio.bot.commands.ip;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;

public class Dec2IpCommand implements Command {

    @Override
    public String name() {
        return "dec2ip";
    }

    @Override
    public String description() {
        return "Конвертация IP из десятичного в IPv4.";
    }

    @Override
    public Category category() {
        return Category.IP;
    }

    @Override
    public void execute(CommandContext ctx) {
        String arg = ctx.argsTrimmed();
        if (arg.isBlank()) {
            ctx.reply("Использование: /dec2ip <число>");
            return;
        }
        try {
            long v = Long.parseLong(arg);
            if (v < 0 || v > 4294967295L) {
                ctx.reply("Число вне диапазона IPv4 (0..4294967295).");
                return;
            }
            String ip = ((v >> 24) & 0xFF) + "." + ((v >> 16) & 0xFF) + "." + ((v >> 8) & 0xFF) + "." + (v & 0xFF);
            ctx.reply(arg + " → " + ip);
        } catch (NumberFormatException e) {
            ctx.reply("Некорректное число.");
        }
    }
}
