package com.helio.bot.commands.minecraft;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.minecraft.MinecraftService;
import com.helio.bot.minecraft.PingResult;
import com.helio.bot.minecraft.ServerRepository;
import com.helio.bot.minecraft.StatsRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class McStatsCommand implements Command {

    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    private final ServerRepository servers;
    private final StatsRepository stats;
    private final MinecraftService minecraft;

    @Override
    public String name() {
        return "mcstats";
    }

    @Override
    public String description() {
        return "Получение статистики сервера Minecraft (Java, Bedrock).";
    }

    @Override
    public Category category() {
        return Category.MINECRAFT;
    }

    @Override
    public void execute(CommandContext ctx) {
        String arg = ctx.argsTrimmed();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mcstats <хост|алиас>");
            return;
        }
        String host = servers.resolve(ctx.userId(), arg);
        int hours = stats.getHours(host);
        long from = System.currentTimeMillis() / 1000 - (long) hours * 3600;
        StatsRepository.Stats st = stats.stats(host, from);

        String live;
        try {
            PingResult r = minecraft.ping(host);
            live = r.online + "/" + r.max;
        } catch (Exception e) {
            live = "недоступен";
        }

        if (st.count == 0) {
            ctx.reply("📊 " + host + "\nТекущий онлайн: " + live
                    + "\n\nИстория ещё не собрана — статистика копится автоматически (раз в 5 мин). "
                    + "Загляни позже для min/avg/max за " + hours + " ч.");
            return;
        }
        ctx.reply("📊 Статистика: " + host + " (за " + hours + " ч)\n"
                + "Текущий онлайн: " + live + "\n"
                + "Минимум: " + st.min + "\n"
                + "Средний: " + String.format("%.1f", st.avg) + "\n"
                + "Рекорд: " + st.record + " (" + TS.format(Instant.ofEpochSecond(st.peakTs)) + ")\n"
                + "Слотов: " + st.slots + "\n"
                + "Замеров: " + st.count);
    }
}
