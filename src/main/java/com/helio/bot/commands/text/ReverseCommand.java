package com.helio.bot.commands.text;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.text.TextTransforms;

public class ReverseCommand implements Command {

    @Override
    public String name() {
        return "reverse";
    }

    @Override
    public String description() {
        return "Разворот текста (вба).";
    }

    @Override
    public Category category() {
        return Category.TEXT;
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (!ctx.hasArgs()) {
            ctx.reply("Использование: /reverse <текст>");
            return;
        }
        ctx.reply(TextTransforms.reverse(ctx.argsTrimmed()));
    }
}
