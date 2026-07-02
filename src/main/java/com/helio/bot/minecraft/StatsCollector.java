package com.helio.bot.minecraft;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Фоновый сбор онлайна: раз в 5 минут пингует все добавленные серверы и пишет замеры. */
@Slf4j
@RequiredArgsConstructor
public class StatsCollector {

    private final ServerRepository servers;
    private final StatsRepository stats;
    private final MinecraftService minecraft;
    private ScheduledExecutorService exec;

    public synchronized void start() {
        if (exec != null) {
            return;
        }
        exec = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "stats-collector");
            t.setDaemon(true);
            return t;
        });
        exec.scheduleAtFixedRate(this::collectSafe, 10, 300, TimeUnit.SECONDS);
        log.info("Сборщик статистики Minecraft запущен (интервал 5 мин)");
    }

    private void collectSafe() {
        try {
            for (String host : servers.allHosts()) {
                try {
                    PingResult r = minecraft.ping(host);
                    stats.record(host, r.online, r.max);
                } catch (Exception ignored) {
                    // сервер недоступен на этом интервале
                }
            }
        } catch (Exception e) {
            log.warn("Ошибка сбора статистики", e);
        }
    }
}
