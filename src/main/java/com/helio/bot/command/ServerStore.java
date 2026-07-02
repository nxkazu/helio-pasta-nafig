package com.helio.bot.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ServerStore {
    private ServerStore() {
    }

    private static final Gson GSON = new Gson();
    private static final Path FILE = Paths.get(System.getProperty("user.home"), ".helio-bot-servers.json");

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Server {
        public String host;
        public String alias;
    }

    private static synchronized Map<String, List<Server>> load() {
        try {
            if (Files.exists(FILE)) {
                String json = new String(Files.readAllBytes(FILE), StandardCharsets.UTF_8);
                Map<String, List<Server>> map = GSON.fromJson(json,
                        new TypeToken<Map<String, List<Server>>>() {
                        }.getType());
                if (map != null) {
                    return map;
                }
            }
        } catch (Exception ignored) {
        }
        return new HashMap<>();
    }

    private static synchronized void save(Map<String, List<Server>> map) {
        try {
            Files.write(FILE, GSON.toJson(map).getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    public static synchronized List<Server> list(String userId) {
        return load().getOrDefault(userId, new ArrayList<>());
    }

    public static synchronized boolean add(String userId, String host, String alias) {
        Map<String, List<Server>> map = load();
        List<Server> servers = map.computeIfAbsent(userId, k -> new ArrayList<>());
        for (Server s : servers) {
            if (s.host.equalsIgnoreCase(host)) {
                return false;
            }
        }
        servers.add(new Server(host, alias));
        save(map);
        return true;
    }

    public static synchronized boolean remove(String userId, String hostOrAlias) {
        Map<String, List<Server>> map = load();
        List<Server> servers = map.get(userId);
        if (servers == null) {
            return false;
        }
        boolean removed = servers.removeIf(s -> s.host.equalsIgnoreCase(hostOrAlias)
                || (s.alias != null && s.alias.equalsIgnoreCase(hostOrAlias)));
        if (removed) {
            save(map);
        }
        return removed;
    }

    public static synchronized boolean setAlias(String userId, String host, String alias) {
        Map<String, List<Server>> map = load();
        List<Server> servers = map.get(userId);
        if (servers == null) {
            return false;
        }
        for (Server s : servers) {
            if (s.host.equalsIgnoreCase(host)) {
                s.alias = alias;
                save(map);
                return true;
            }
        }
        return false;
    }
}
