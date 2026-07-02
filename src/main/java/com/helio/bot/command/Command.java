package com.helio.bot.command;

@FunctionalInterface
public interface Command {
    void run(Ctx ctx) throws Exception;
}
