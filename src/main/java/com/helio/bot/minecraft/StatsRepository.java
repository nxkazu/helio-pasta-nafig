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
import java.util.List;
import java.util.Map;

/** Хранилище исторических замеров онлайна и настроек окна графика. */
public class StatsRepository {

    private static final Gson GSON = new Gson();
    private static final int MAX_SAMPLES = 4032; // ~14 суток при интервале 5 мин
    private final Path file = Paths.get(System.getProperty("user.home"), ".helio-bot-stats.json");
    private final Path settings = Paths.get(System.getProperty("user.home"), ".helio-bot-graph.json");

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sample {
        public long ts;
        public int online;
        public int max;
    }

    public static class Stats {
        public int min;
        public double avg;
        public int record;
        public int slots;
        public int current;
        public int count;
        public long peakTs;
    }

    private synchronized Map<String, List<Sample>> load() {
        try {
            if (Files.exists(file)) {
                Map<String, List<Sample>> m = GSON.fromJson(
                        new String(Files.readAllBytes(file), StandardCharsets.UTF_8),
                        new TypeToken<Map<String, List<Sample>>>() {
                        }.getType());
                if (m != null) {
                    return m;
                }
            }
        } catch (Exception ignored) {
        }
        return new HashMap<>();
    }

    private synchronized void save(Map<String, List<Sample>> m) {
        try {
            Files.write(file, GSON.toJson(m).getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    public synchronized void record(String host, int online, int max) {
        Map<String, List<Sample>> m = load();
        List<Sample> l = m.computeIfAbsent(host, k -> new ArrayList<>());
        l.add(new Sample(System.currentTimeMillis() / 1000, online, max));
        while (l.size() > MAX_SAMPLES) {
            l.remove(0);
        }
        save(m);
    }

    public synchronized List<Sample> samples(String host, long fromTs) {
        List<Sample> out = new ArrayList<>();
        for (Sample s : load().getOrDefault(host, new ArrayList<>())) {
            if (s.ts >= fromTs) {
                out.add(s);
            }
        }
        return out;
    }

    public synchronized Stats stats(String host, long fromTs) {
        List<Sample> l = samples(host, fromTs);
        Stats st = new Stats();
        st.count = l.size();
        if (l.isEmpty()) {
            return st;
        }
        int min = Integer.MAX_VALUE;
        int rec = 0;
        long sum = 0;
        long peakTs = 0;
        int slots = 0;
        for (Sample s : l) {
            min = Math.min(min, s.online);
            if (s.online > rec) {
                rec = s.online;
                peakTs = s.ts;
            }
            sum += s.online;
            slots = Math.max(slots, s.max);
        }
        st.min = min;
        st.record = rec;
        st.avg = (double) sum / l.size();
        st.slots = slots;
        st.peakTs = peakTs;
        st.current = l.get(l.size() - 1).online;
        return st;
    }

    private synchronized Map<String, Double> loadSettings() {
        try {
            if (Files.exists(settings)) {
                Map<String, Double> m = GSON.fromJson(
                        new String(Files.readAllBytes(settings), StandardCharsets.UTF_8),
                        new TypeToken<Map<String, Double>>() {
                        }.getType());
                if (m != null) {
                    return m;
                }
            }
        } catch (Exception ignored) {
        }
        return new HashMap<>();
    }

    public synchronized int getHours(String host) {
        Double v = loadSettings().get(host);
        return v == null ? 24 : v.intValue();
    }

    public synchronized void setHours(String host, int hours) {
        Map<String, Double> m = loadSettings();
        m.put(host, (double) hours);
        try {
            Files.write(settings, GSON.toJson(m).getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }
}
