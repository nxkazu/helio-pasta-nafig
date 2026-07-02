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
public class McCompareCommand implements Command {

    private final ServerRepository servers;
    private final StatsRepository stats;

    @Override
    public String name() {
        return "mccompare";
    }

    @Override
    public String description() {
        return "Сравнить статистику серверов Minecraft (Java, Bedrock). ᵇᵉᵗᵃ";
    }

    @Override
    public Category category() {
        return Category.MINECRAFT;
    }

    @Override
    public void execute(CommandContext ctx) {
        String[] a = ctx.argList();
        if (a.length < 2) {
            ctx.reply("Использование: /mccompare <сервер1> <сервер2> [сервер3 ...]");
            return;
        }
        int hours = 24;
        long to = System.currentTimeMillis() / 1000;
        long from = to - (long) hours * 3600;
        Map<String, List<StatsRepository.Sample>> series = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder("🆚 Сравнение (за " + hours + " ч):\n");
        boolean any = false;
        for (String token : a) {
            String host = servers.resolve(ctx.userId(), token);
            series.put(host, stats.samples(host, from));
            StatsRepository.Stats st = stats.stats(host, from);
            if (st.count > 0) {
                any = true;
                sb.append("• ").append(host).append(": avg ").append(String.format("%.1f", st.avg))
                        .append(", рекорд ").append(st.record).append("\n");
            } else {
                sb.append("• ").append(host).append(": нет данных\n");
            }
        }
        if (!any) {
            ctx.reply(sb + "\nИстория ещё не собрана — статистика копится автоматически.");
            return;
        }
        try {
            byte[] png = GraphRenderer.render("Сравнение серверов (" + hours + " ч)", series, from, to);
            ctx.replyPhoto(png, sb.toString());
        } catch (Exception e) {
            ctx.reply(sb.toString());
        }
    }
}
