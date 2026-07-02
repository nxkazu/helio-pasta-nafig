package com.helio.bot.commands.media;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.render.QuoteRenderer;

public class QuoteCommand implements Command {

    @Override
    public String name() {
        return "quote";
    }

    @Override
    public String description() {
        return "Генерация изображения цитаты.";
    }

    @Override
    public Category category() {
        return Category.MEDIA;
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        String text = ctx.argsTrimmed();
        String author = null;
        if (text.isBlank() && ctx.getMessage().getReplyToMessage() != null
                && ctx.getMessage().getReplyToMessage().hasText()) {
            text = ctx.getMessage().getReplyToMessage().getText();
            if (ctx.getMessage().getReplyToMessage().getFrom() != null) {
                author = ctx.getMessage().getReplyToMessage().getFrom().getFirstName();
            }
        }
        if (text.isBlank()) {
            ctx.reply("Использование: /quote <текст> или ответом на сообщение");
            return;
        }
        ctx.replyPhoto(QuoteRenderer.render(text, author), null);
    }
}
