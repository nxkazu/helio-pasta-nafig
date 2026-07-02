package com.helio.bot.core;

import java.util.List;

/** Контракт одной команды бота. Каждая команда — отдельный класс. */
public interface Command {
    String name();

    String description();

    Category category();

    void execute(CommandContext ctx) throws Exception;

    default List<String> aliases() {
        return List.of();
    }
}
