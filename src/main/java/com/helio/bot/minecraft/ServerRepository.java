package com.helio.bot.minecraft;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Хранилище серверов пользователей (JSON в домашней директории). */
public class ServerRepository {

    private static final Gson GSON = new Gson();
    private final Path file = Paths.get(System.getProperty("user.home"), ".helio-bot-servers.json");

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Server {
        public String host;
        public String alias;
    }

    private synchronized Map<String, List<Server>> load() {
        try {
            if (Files.exists(file)) {
                Map<String, List<Server>> map = GSON.fromJson(
                        new String(Files.readAllBytes(file), StandardCharsets.UTF_8),
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

    private synchronized void save(Map<String, List<Server>> map) {
        try {
            Files.write(file, GSON.toJson(map).getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    public synchronized List<Server> list(String userId) {
        return load().getOrDefault(userId, new ArrayList<>());
    }

    public synchronized boolean add(String userId, String host, String alias) {
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

    public synchronized boolean remove(String userId, String hostOrAlias) {
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

    public synchronized boolean setAlias(String userId, String host, String alias) {
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

    public synchronized List<String> allHosts() {
        Set<String> hosts = new LinkedHashSet<>();
        for (List<Server> l : load().values()) {
            for (Server s : l) {
                hosts.add(s.host);
            }
        }
        return new ArrayList<>(hosts);
    }

    public synchronized String resolve(String userId, String token) {
        for (Server s : list(userId)) {
            if (s.host.equalsIgnoreCase(token) || (s.alias != null && s.alias.equalsIgnoreCase(token))) {
                return s.host;
            }
        }
        return token;
    }
}
