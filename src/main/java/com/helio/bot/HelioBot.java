package com.helio.bot;

import com.helio.bot.command.CommandDef;
import com.helio.bot.command.Commands;
import com.helio.bot.command.Ctx;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;
import java.util.Map;

@Slf4j
public class HelioBot extends TelegramLongPollingBot {

    private final String username;
    @Getter
    private final Map<String, CommandDef> commands;

    public HelioBot(String token, String username) {
        super(token);
        this.username = username;
        this.commands = Commands.all();
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

        String withoutSlash = text.substring(1);
        String[] parts = withoutSlash.split("\\s+", 2);
        String cmdRaw = parts[0];
        int at = cmdRaw.indexOf('@');
        if (at >= 0) {
            cmdRaw = cmdRaw.substring(0, at);
        }
        String cmd = cmdRaw.toLowerCase();
        String argLine = parts.length > 1 ? parts[1] : "";

        Ctx ctx = new Ctx(this, update, message, argLine);
        CommandDef def = commands.get(cmd);
        if (def == null) {
            ctx.reply("Неизвестная команда. /help — список команд.");
            return;
        }
        try {
            def.handler().run(ctx);
        } catch (Exception e) {
            log.error("Команда /{} завершилась ошибкой", cmd, e);
            ctx.reply("Ошибка при выполнении команды: " + e.getMessage());
        }
    }

    public void send(String chatId, String textToSend) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(textToSend)
                    .disableWebPagePreview(true)
                    .build());
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
