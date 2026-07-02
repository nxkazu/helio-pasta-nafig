package com.helio.bot.commands.minecraft;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.minecraft.ServerRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class McAddServerCommand implements Command {

    private final ServerRepository servers;

    @Override
    public String name() {
        return "mcaddserver";
    }

    @Override
    public String description() {
        return "Добавление сервера Minecraft (Java, Bedrock) в сбор статистики.";
    }

    @Override
    public Category category() {
        return Category.MINECRAFT;
    }

    @Override
    public void execute(CommandContext ctx) {
        String arg = ctx.argsTrimmed();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mcaddserver <хост>[:порт] [алиас]");
            return;
        }
        String[] a = arg.split("\\s+", 2);
        String alias = a.length > 1 ? a[1] : null;
        boolean ok = servers.add(ctx.userId(), a[0], alias);
        ctx.reply(ok
                ? "✅ Сервер добавлен: " + a[0] + (alias != null ? " (" + alias + ")" : "")
                + "\nСтатистика онлайна начнёт собираться автоматически."
                : "⚠️ Такой сервер уже добавлен.");
    }
}
