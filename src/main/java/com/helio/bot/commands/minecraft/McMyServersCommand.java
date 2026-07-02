package com.helio.bot.commands.minecraft;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.minecraft.ServerRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class McMyServersCommand implements Command {

    private final ServerRepository servers;

    @Override
    public String name() {
        return "mcmyservers";
    }

    @Override
    public String description() {
        return "Вывод списка добавленных серверов Minecraft.";
    }

    @Override
    public Category category() {
        return Category.MINECRAFT;
    }

    @Override
    public void execute(CommandContext ctx) {
        List<ServerRepository.Server> list = servers.list(ctx.userId());
        if (list.isEmpty()) {
            ctx.reply("У вас нет добавленных серверов. /mcaddserver");
            return;
        }
        StringBuilder sb = new StringBuilder("🗄 Ваши серверы:\n");
        for (ServerRepository.Server s : list) {
            sb.append("• ").append(s.host).append(s.alias != null ? " (" + s.alias + ")" : "").append("\n");
        }
        ctx.reply(sb.toString());
    }
}
