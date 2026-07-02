package com.helio.bot.command;

import com.helio.bot.HelioBot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@RequiredArgsConstructor
public class Ctx {
    private final HelioBot bot;
    private final Update update;
    private final Message message;
    private final String argLine;

    public String chatId() {
        return message.getChatId().toString();
    }

    public boolean hasArgs() {
        return argLine != null && !argLine.isBlank();
    }

    public String userId() {
        return message.getFrom() != null ? String.valueOf(message.getFrom().getId()) : "0";
    }

    public void reply(String text) {
        if (text == null) {
            text = "";
        }
        if (text.length() > 4096) {
            text = text.substring(0, 4090) + "…";
        }
        bot.send(chatId(), text);
    }

    public void replyPhoto(byte[] png, String caption) {
        bot.sendPhoto(chatId(), png, caption);
    }
}
