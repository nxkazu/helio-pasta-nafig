package com.helio.bot.commands.net;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.util.Whois;

public class WhoisCommand implements Command {

    @Override
    public String name() {
        return "whois";
    }

    @Override
    public String description() {
        return "Найти WHOIS информацию адреса или домена.";
    }

    @Override
    public Category category() {
        return Category.NETWORK;
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        String q = ctx.argsTrimmed();
        if (q.isBlank()) {
            ctx.reply("Использование: /whois <домен|IP>");
            return;
        }
        String out = Whois.lookup(q);
        if (out.length() > 3500) {
            out = out.substring(0, 3500) + "\n…";
        }
        ctx.reply(out.isBlank() ? "Нет данных." : out);
    }
}
