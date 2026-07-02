package com.helio.bot.commands.text;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.text.TextTransforms;

public class RuneCommand implements Command {

    @Override
    public String name() {
        return "rune";
    }

    @Override
    public String description() {
        return "Конвертация текста в руны (ᚨᛒᚹ).";
    }

    @Override
    public Category category() {
        return Category.TEXT;
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (!ctx.hasArgs()) {
            ctx.reply("Использование: /rune <текст>");
            return;
        }
        ctx.reply(TextTransforms.rune(ctx.argsTrimmed()));
    }
}
