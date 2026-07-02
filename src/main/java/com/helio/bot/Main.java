package com.helio.bot;

import com.helio.bot.core.BotBootstrap;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/** Точка входа: собирает зависимости через BotBootstrap, регистрирует бота и запускает сбор статистики. */
@Slf4j
public class Main {
    public static void main(String[] args) throws Exception {
        String token = System.getenv("BOT_TOKEN");
        String username = System.getenv().getOrDefault("BOT_USERNAME", "helio_pasta_bot");
        if (token == null || token.isBlank()) {
            log.error("Переменная окружения BOT_TOKEN обязательна");
            System.exit(1);
        }
        BotBootstrap bootstrap = new BotBootstrap();
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(new HelioBot(token, username, bootstrap.registry()));
        bootstrap.statsCollector().start();
        log.info("HelioBot запущен как @{} ({} команд)", username, bootstrap.registry().all().size());
    }
}
