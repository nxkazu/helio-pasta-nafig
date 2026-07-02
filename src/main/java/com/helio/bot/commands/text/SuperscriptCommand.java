package com.helio.bot.commands.text;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.text.TextTransforms;

public class SuperscriptCommand implements Command {

    @Override
    public String name() {
        return "superscript";
    }

    @Override
    public String description() {
        return "Конвертация латинских символов в мелкие верхние (ᵃᵇᶜ).";
    }

    @Override
    public Category category() {
        return Category.TEXT;
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (!ctx.hasArgs()) {
            ctx.reply("Использование: /superscript <текст>");
            return;
        }
        ctx.reply(TextTransforms.superscript(ctx.argsTrimmed()));
    }
}
