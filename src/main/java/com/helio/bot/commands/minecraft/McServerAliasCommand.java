package com.helio.bot.commands.minecraft;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.minecraft.ServerRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class McServerAliasCommand implements Command {

    private final ServerRepository servers;

    @Override
    public String name() {
        return "mcserveralias";
    }

    @Override
    public String description() {
        return "Установка алиаса для добавленного сервера Minecraft.";
    }

    @Override
    public Category category() {
        return Category.MINECRAFT;
    }

    @Override
    public void execute(CommandContext ctx) {
        String[] a = ctx.argsTrimmed().split("\\s+", 2);
        if (a.length < 2) {
            ctx.reply("Использование: /mcserveralias <хост> <алиас>");
            return;
        }
        ctx.reply(servers.setAlias(ctx.userId(), a[0], a[1]) ? "✅ Алиас установлен." : "Сервер не найден.");
    }
}
