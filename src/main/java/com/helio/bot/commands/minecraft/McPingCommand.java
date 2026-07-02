package com.helio.bot.commands.minecraft;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.minecraft.MinecraftService;
import com.helio.bot.minecraft.PingResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class McPingCommand implements Command {

    private final MinecraftService minecraft;

    @Override
    public String name() {
        return "mcping";
    }

    @Override
    public String description() {
        return "Информация о сервере Minecraft (Java, Bedrock).";
    }

    @Override
    public Category category() {
        return Category.MINECRAFT;
    }

    @Override
    public void execute(CommandContext ctx) {
        String arg = ctx.argsTrimmed();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mcping <хост>[:порт]");
            return;
        }
        try {
            PingResult r = minecraft.ping(arg);
            String motd = r.motd == null ? "-" : r.motd.replaceAll("\u00A7.", "");
            ctx.reply("🟢 " + r.host + ":" + r.port + " [" + r.edition + "]\n"
                    + "Версия: " + (r.versionName == null ? "?" : r.versionName) + "\n"
                    + "Онлайн: " + r.online + "/" + r.max + "\n"
                    + "MOTD: " + motd + "\n"
                    + "Пинг: " + r.pingMs + " мс");
        } catch (Exception e) {
            ctx.reply("🔴 " + arg + " — сервер недоступен (проверены Java и Bedrock).");
        }
    }
}
