package com.helio.bot.commands.ip;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;

public class IpConvertCommand implements Command {

    @Override
    public String name() {
        return "ipconvert";
    }

    @Override
    public String description() {
        return "Конвертация IPv4 в различные форматы.";
    }

    @Override
    public Category category() {
        return Category.IP;
    }

    @Override
    public void execute(CommandContext ctx) {
        String arg = ctx.argsTrimmed();
        String[] parts = arg.split("\\.");
        if (parts.length != 4) {
            ctx.reply("Использование: /ipconvert <IPv4>");
            return;
        }
        try {
            long v = 0;
            for (String p : parts) {
                int o = Integer.parseInt(p);
                if (o < 0 || o > 255) {
                    throw new NumberFormatException();
                }
                v = (v << 8) | o;
            }
            ctx.reply("IP: " + arg + "\n"
                    + "DEC: " + v + "\n"
                    + "HEX: 0x" + Long.toHexString(v).toUpperCase() + "\n"
                    + "OCT: 0" + Long.toOctalString(v) + "\n"
                    + "BIN: " + Long.toBinaryString(v));
        } catch (NumberFormatException e) {
            ctx.reply("Некорректный IPv4.");
        }
    }
}
