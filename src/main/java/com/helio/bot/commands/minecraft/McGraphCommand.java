package com.helio.bot.commands.minecraft;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.minecraft.ServerRepository;
import com.helio.bot.minecraft.StatsRepository;
import com.helio.bot.render.GraphRenderer;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class McGraphCommand implements Command {

    private final ServerRepository servers;
    private final StatsRepository stats;

    @Override
    public String name() {
        return "mcgraph";
    }

    @Override
    public String description() {
        return "Изменение настроек графика сервера Minecraft.";
    }

    @Override
    public Category category() {
        return Category.MINECRAFT;
    }

    @Override
    public void execute(CommandContext ctx) {
        String[] a = ctx.argList();
        if (a.length == 0) {
            ctx.reply("Использование: /mcgraph <хост|алиас> [часов]\nНапр.: /mcgraph mc.artygrief.su 48");
            return;
        }
        String host = servers.resolve(ctx.userId(), a[0]);
        int hours = stats.getHours(host);
        if (a.length > 1) {
            try {
                hours = Math.max(1, Math.min(720, Integer.parseInt(a[1])));
                stats.setHours(host, hours);
            } catch (NumberFormatException ignored) {
            }
        }
        long to = System.currentTimeMillis() / 1000;
        long from = to - (long) hours * 3600;
        List<StatsRepository.Sample> pts = stats.samples(host, from);
        if (pts.isEmpty()) {
            ctx.reply("📈 По " + host + " ещё нет собранных данных за " + hours + " ч. "
                    + "Статистика копится автоматически — попробуй позже.\n"
                    + "(Окно графика установлено: " + hours + " ч.)");
            return;
        }
        try {
            Map<String, List<StatsRepository.Sample>> series = new LinkedHashMap<>();
            series.put(host, pts);
            byte[] png = GraphRenderer.render("Онлайн: " + host + " (" + hours + " ч)", series, from, to);
            ctx.replyPhoto(png, "📈 " + host + " — онлайн за " + hours + " ч");
        } catch (Exception e) {
            ctx.reply("Не удалось построить график: " + e.getMessage());
        }
    }
}
