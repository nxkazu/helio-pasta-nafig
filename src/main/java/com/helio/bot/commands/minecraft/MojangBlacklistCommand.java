package com.helio.bot.commands.minecraft;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.minecraft.HostPort;
import com.helio.bot.minecraft.MojangBlacklistService;
import com.helio.bot.minecraft.ServerRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MojangBlacklistCommand implements Command {

    private final ServerRepository servers;
    private final MojangBlacklistService mojang;

    @Override
    public String name() {
        return "mojangblacklist";
    }

    @Override
    public String description() {
        return "Наличие сервера Minecraft в ЧС Mojang.";
    }

    @Override
    public Category category() {
        return Category.MINECRAFT;
    }

    @Override
    public void execute(CommandContext ctx) {
        String arg = ctx.argsTrimmed();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mojangblacklist <хост>");
            return;
        }
        String host = HostPort.parse(servers.resolve(ctx.userId(), arg), -1).host();
        try {
            boolean blocked = mojang.isBlocked(host);
            ctx.reply(blocked
                    ? "🚫 " + host + " — В чёрном списке Mojang."
                    : "✅ " + host + " — НЕ в чёрном списке Mojang.");
        } catch (Exception e) {
            ctx.reply("Не удалось проверить ЧС Mojang: " + e.getMessage());
        }
    }
}
