package com.helio.bot.commands.text;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;
import com.helio.bot.text.TextTransforms;

public class SmallCapsCommand implements Command {

    @Override
    public String name() {
        return "smallcaps";
    }

    @Override
    public String description() {
        return "Конвертация латинских символов в маленькие прописные (ᴀʙᴄ).";
    }

    @Override
    public Category category() {
        return Category.TEXT;
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (!ctx.hasArgs()) {
            ctx.reply("Использование: /smallcaps <текст>");
            return;
        }
        ctx.reply(TextTransforms.smallcaps(ctx.argsTrimmed()));
    }
}
