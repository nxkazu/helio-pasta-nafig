package com.helio.bot.core;

import com.helio.bot.HelioBot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/** Всё, что нужно команде для работы: доступ к сообщению, аргументам и способам ответа. */
@Getter
@RequiredArgsConstructor
public class CommandContext {
    private final HelioBot bot;
    private final Update update;
    private final Message message;
    private final String args;

    public String chatId() {
        return message.getChatId().toString();
    }

    public String userId() {
        return message.getFrom() != null ? String.valueOf(message.getFrom().getId()) : "0";
    }

    public boolean hasArgs() {
        return args != null && !args.isBlank();
    }

    public String argsTrimmed() {
        return args == null ? "" : args.trim();
    }

    public String[] argList() {
        return hasArgs() ? argsTrimmed().split("\\s+") : new String[0];
    }

    public void reply(String text) {
        bot.send(chatId(), text);
    }

    public void replyPhoto(byte[] png, String caption) {
        bot.sendPhoto(chatId(), png, caption);
    }
}
