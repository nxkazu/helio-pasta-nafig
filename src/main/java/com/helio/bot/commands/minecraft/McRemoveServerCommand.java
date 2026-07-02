package com.helio.bot.commands.minecraft;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.minecraft.ServerRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class McRemoveServerCommand implements Command {

    private final ServerRepository servers;

    @Override
    public String name() {
        return "mcremoveserver";
    }

    @Override
    public String description() {
        return "Удаление сервера Minecraft из сбора статистики.";
    }

    @Override
    public Category category() {
        return Category.MINECRAFT;
    }

    @Override
    public void execute(CommandContext ctx) {
        String arg = ctx.argsTrimmed();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mcremoveserver <хост|алиас>");
            return;
        }
        ctx.reply(servers.remove(ctx.userId(), arg) ? "🗑 Удалено." : "Не найдено.");
    }
}
