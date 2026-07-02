package com.helio.bot.commands.text;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.text.TextTransforms;

public class RotateCommand implements Command {

    @Override
    public String name() {
        return "rotate";
    }

    @Override
    public String description() {
        return "Переворот текста (ɐƍʚ).";
    }

    @Override
    public Category category() {
        return Category.TEXT;
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (!ctx.hasArgs()) {
            ctx.reply("Использование: /rotate <текст>");
            return;
        }
        ctx.reply(TextTransforms.rotate(ctx.argsTrimmed()));
    }
}
