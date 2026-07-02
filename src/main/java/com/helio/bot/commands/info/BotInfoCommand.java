package com.helio.bot.commands.info;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.core.CommandRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BotInfoCommand implements Command {

    private final CommandRegistry registry;

    @Override
    public String name() {
        return "bot";
    }

    @Override
    public String description() {
        return "Информация о боте.";
    }

    @Override
    public Category category() {
        return Category.INFO;
    }

    @Override
    public void execute(CommandContext ctx) {
        ctx.reply("🌵 Helio Pasta Nafig Bot\n"
                + "Версия: 2.0.0\n"
                + "Автор: @nxkazu\n"
                + "Команд: " + registry.all().size() + "\n"
                + "Стек: Java 17 + Lombok, ООП-архитектура команд.\n"
                + "/help — список команд.");
    }
}
