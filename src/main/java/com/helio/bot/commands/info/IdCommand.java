package com.helio.bot.commands.info;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import org.telegram.telegrambots.meta.api.objects.Message;

public class IdCommand implements Command {

    @Override
    public String name() {
        return "id";
    }

    @Override
    public String description() {
        return "Получить ID пользователя / сообщества / бота.";
    }

    @Override
    public Category category() {
        return Category.INFO;
    }

    @Override
    public void execute(CommandContext ctx) {
        Message m = ctx.getMessage();
        StringBuilder sb = new StringBuilder();
        if (m.getFrom() != null) {
            sb.append("Твой ID: ").append(m.getFrom().getId()).append("\n");
        }
        sb.append("Chat ID: ").append(m.getChatId());
        if (m.getReplyToMessage() != null && m.getReplyToMessage().getFrom() != null) {
            sb.append("\nID (ответ): ").append(m.getReplyToMessage().getFrom().getId());
        }
        if (m.getForwardFrom() != null) {
            sb.append("\nID (форвард): ").append(m.getForwardFrom().getId());
        }
        ctx.reply(sb.toString());
    }
}
