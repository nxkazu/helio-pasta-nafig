package com.helio.bot.command;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Периодически пингует все добавленные серверы и пишет замеры онлайна в StatsStore. */
@Slf4j
public final class StatsCollector {
    private StatsCollector() {
    }

    private static ScheduledExecutorService exec;

    public static synchronized void start() {
        if (exec != null) {
            return;
        }
        exec = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "stats-collector");
            t.setDaemon(true);
            return t;
        });
        exec.scheduleAtFixedRate(StatsCollector::collectSafe, 10, 300, TimeUnit.SECONDS);
        log.info("Сборщик статистики Minecraft запущен (интервал 5 мин)");
    }

    private static void collectSafe() {
        try {
            collect();
        } catch (Exception e) {
            log.warn("Ошибка сбора статистики", e);
        }
    }

    private static void collect() {
        for (String host : ServerStore.allHosts()) {
            try {
                String h = host;
                int port = 0;
                if (host.contains(":")) {
                    String[] p = host.split(":");
                    h = p[0];
                    try {
                        port = Integer.parseInt(p[1]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                MinecraftPinger.Result r;
                try {
                    r = MinecraftPinger.pingJava(h, port == 0 ? 25565 : port, 4000);
                } catch (Exception e) {
                    r = BedrockPinger.ping(h, port == 0 ? 19132 : port, 4000);
                }
                StatsStore.record(host, r.online, r.max);
            } catch (Exception ignored) {
                // сервер недоступен на этом интервале — пропускаем
            }
        }
    }
}
