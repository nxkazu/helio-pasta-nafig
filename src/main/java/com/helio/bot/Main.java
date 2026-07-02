package com.helio.bot;

import com.helio.bot.command.StatsCollector;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
public class Main {
    public static void main(String[] args) throws Exception {
        String token = System.getenv("BOT_TOKEN");
        String username = System.getenv().getOrDefault("BOT_USERNAME", "helio_pasta_bot");
        if (token == null || token.isBlank()) {
            log.error("Переменная окружения BOT_TOKEN обязательна");
            System.exit(1);
        }
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(new HelioBot(token, username));
        // Запускаем фоновый сбор статистики Minecraft-серверов (для /mcstats, /mcgraph, /mccompare)
        StatsCollector.start();
        log.info("HelioBot запущен как @{}", username);
    }
}
