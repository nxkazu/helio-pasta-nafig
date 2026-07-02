package com.helio.bot.commands.info;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.core.CommandRegistry;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class HelpCommand implements Command {

    private final CommandRegistry registry;

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "Список команд.";
    }

    @Override
    public Category category() {
        return Category.INFO;
    }

    @Override
    public List<String> aliases() {
        return List.of("commands", "start");
    }

    @Override
    public void execute(CommandContext ctx) {
        StringBuilder sb = new StringBuilder("🌵 Команды бота:\n");
        for (Map.Entry<Category, List<Command>> e : registry.grouped().entrySet()) {
            sb.append("\n").append(e.getKey().label()).append("\n");
            for (Command c : e.getValue()) {
                sb.append("/").append(c.name()).append(" — ").append(c.description()).append("\n");
            }
        }
        ctx.reply(sb.toString());
    }
}
