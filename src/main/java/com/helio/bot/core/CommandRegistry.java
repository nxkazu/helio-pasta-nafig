package com.helio.bot.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** Реестр команд: хранит порядок регистрации, резолвит по имени и алиасам, группирует по категориям. */
public class CommandRegistry {

    private final List<Command> commands = new ArrayList<>();
    private final Map<String, Command> byToken = new LinkedHashMap<>();

    public void register(Command command) {
        commands.add(command);
        byToken.put(command.name().toLowerCase(), command);
        for (String alias : command.aliases()) {
            byToken.put(alias.toLowerCase(), command);
        }
    }

    public Optional<Command> resolve(String token) {
        return Optional.ofNullable(byToken.get(token.toLowerCase()));
    }

    public List<Command> all() {
        return List.copyOf(commands);
    }

    public Map<Category, List<Command>> grouped() {
        Map<Category, List<Command>> map = new TreeMap<>();
        for (Command c : commands) {
            map.computeIfAbsent(c.category(), k -> new ArrayList<>()).add(c);
        }
        return map;
    }
}
