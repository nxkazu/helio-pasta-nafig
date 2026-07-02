package com.helio.bot;

import com.helio.bot.core.CommandContext;
import com.helio.bot.core.CommandRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;

/** Тонкий транспортный слой: принимает апдейты, парсит команду и делегирует в CommandRegistry. */
@Slf4j
public class HelioBot extends TelegramLongPollingBot {

    private final String username;
    @Getter
    private final CommandRegistry registry;

    public HelioBot(String token, String username, CommandRegistry registry) {
        super(token);
        this.username = username;
        this.registry = registry;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        Message message = update.getMessage();
        String text = message.getText().trim();
        if (!text.startsWith("/")) {
            return;
        }
        String[] parts = text.substring(1).split("\\s+", 2);
        String token = parts[0];
        int at = token.indexOf('@');
        if (at >= 0) {
            token = token.substring(0, at);
        }
        String args = parts.length > 1 ? parts[1] : "";
        CommandContext ctx = new CommandContext(this, update, message, args);
        registry.resolve(token).ifPresentOrElse(cmd -> {
            try {
                cmd.execute(ctx);
            } catch (Exception e) {
                log.error("Команда /{} завершилась ошибкой", cmd.name(), e);
                ctx.reply("Ошибка при выполнении команды: " + e.getMessage());
            }
        }, () -> ctx.reply("Неизвестная команда. /help — список команд."));
    }

    public void send(String chatId, String text) {
        if (text == null) {
            text = "";
        }
        if (text.length() > 4096) {
            text = text.substring(0, 4090) + "…";
        }
        try {
            execute(SendMessage.builder().chatId(chatId).text(text).disableWebPagePreview(true).build());
        } catch (Exception e) {
            log.error("Не удалось отправить сообщение", e);
        }
    }

    public void sendPhoto(String chatId, byte[] png, String caption) {
        try {
            execute(SendPhoto.builder()
                    .chatId(chatId)
                    .photo(new InputFile(new ByteArrayInputStream(png), "image.png"))
                    .caption(caption)
                    .build());
        } catch (Exception e) {
            log.error("Не удалось отправить фото", e);
        }
    }
}
