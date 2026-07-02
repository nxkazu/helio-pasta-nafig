package com.helio.bot.commands.info;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.core.CommandRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AliasesCommand implements Command {

    private final CommandRegistry registry;

    @Override
    public String name() {
        return "aliases";
    }

    @Override
    public String description() {
        return "Алиасы для команд.";
    }

    @Override
    public Category category() {
        return Category.INFO;
    }

    @Override
    public void execute(CommandContext ctx) {
        StringBuilder sb = new StringBuilder("🔗 Алиасы команд:\n");
        boolean any = false;
        for (Command c : registry.all()) {
            if (!c.aliases().isEmpty()) {
                any = true;
                sb.append("/").append(c.name()).append(" → ")
                        .append(String.join(", ", c.aliases())).append("\n");
            }
        }
        ctx.reply(any ? sb.toString() : "Пока ни у одной команды нет алиасов.");
    }
}
