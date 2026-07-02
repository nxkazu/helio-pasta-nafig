package com.helio.bot.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class McCommands {
    private McCommands() {
    }

    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    // ------------------------------------------------------------------
    // Ping (Java + Bedrock fallback)
    // ------------------------------------------------------------------

    static void mcping(Ctx ctx) {
        String arg = ctx.getArgLine().trim();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mcping <хост>[:порт]");
            return;
        }
        String host = arg;
        int port = -1;
        if (arg.contains(":")) {
            String[] p = arg.split(":");
            host = p[0];
            try {
                port = Integer.parseInt(p[1]);
            } catch (NumberFormatException ignored) {
            }
        }
        try {
            MinecraftPinger.Result r = MinecraftPinger.pingJava(host, port < 0 ? 25565 : port, 5000);
            ctx.reply(format(host, port < 0 ? 25565 : port, "Java", r));
            return;
        } catch (Exception ignored) {
        }
        try {
            MinecraftPinger.Result r = BedrockPinger.ping(host, port < 0 ? 19132 : port, 5000);
            ctx.reply(format(host, port < 0 ? 19132 : port, "Bedrock", r));
        } catch (Exception e) {
            ctx.reply("🔴 " + host + " — сервер недоступен (проверены Java и Bedrock).");
        }
    }

    private static String format(String host, int port, String edition, MinecraftPinger.Result r) {
        String motd = r.motd == null ? "-" : r.motd.replaceAll("\u00A7.", "");
        return "🟢 " + host + ":" + port + " [" + edition + "]\n"
                + "Версия: " + (r.versionName == null ? "?" : r.versionName) + "\n"
                + "Онлайн: " + r.online + "/" + r.max + "\n"
                + "MOTD: " + motd + "\n"
                + "Пинг: " + r.pingMs + " мс";
    }

    // ------------------------------------------------------------------
    // Server list management
    // ------------------------------------------------------------------

    static void mcaddserver(Ctx ctx) {
        String arg = ctx.getArgLine().trim();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mcaddserver <хост>[:порт] [алиас]");
            return;
        }
        String[] a = arg.split("\\s+", 2);
        String alias = a.length > 1 ? a[1] : null;
        boolean ok = ServerStore.add(ctx.userId(), a[0], alias);
        ctx.reply(ok
                ? "✅ Сервер добавлен: " + a[0] + (alias != null ? " (" + alias + ")" : "")
                + "\nСтатистика онлайна начнёт собираться автоматически."
                : "⚠️ Такой сервер уже добавлен.");
    }

    static void mcmyservers(Ctx ctx) {
        List<ServerStore.Server> list = ServerStore.list(ctx.userId());
        if (list.isEmpty()) {
            ctx.reply("У вас нет добавленных серверов. /mcaddserver");
            return;
        }
        StringBuilder sb = new StringBuilder("🗄 Ваши серверы:\n");
        for (ServerStore.Server s : list) {
            sb.append("• ").append(s.host).append(s.alias != null ? " (" + s.alias + ")" : "").append("\n");
        }
        ctx.reply(sb.toString());
    }

    static void mcremoveserver(Ctx ctx) {
        String arg = ctx.getArgLine().trim();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mcremoveserver <хост|алиас>");
            return;
        }
        ctx.reply(ServerStore.remove(ctx.userId(), arg) ? "🗑 Удалено." : "Не найдено.");
    }

    static void mcserveralias(Ctx ctx) {
        String[] a = ctx.getArgLine().trim().split("\\s+", 2);
        if (a.length < 2) {
            ctx.reply("Использование: /mcserveralias <хост> <алиас>");
            return;
        }
        ctx.reply(ServerStore.setAlias(ctx.userId(), a[0], a[1]) ? "✅ Алиас установлен." : "Сервер не найден.");
    }

    // ------------------------------------------------------------------
    // Stats / graph / compare
    // ------------------------------------------------------------------

    static void mcstats(Ctx ctx) {
        String arg = ctx.getArgLine().trim();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mcstats <хост|алиас>");
            return;
        }
        String host = ServerStore.resolve(ctx.userId(), arg);
        int hours = StatsStore.getHours(host);
        long from = System.currentTimeMillis() / 1000 - (long) hours * 3600;
        StatsStore.Stats st = StatsStore.stats(host, from);

        String live;
        try {
            MinecraftPinger.Result r = MinecraftPinger.pingJava(hostOnly(host), hostPort(host, 25565), 4000);
            live = r.online + "/" + r.max;
        } catch (Exception e) {
            try {
                MinecraftPinger.Result r = BedrockPinger.ping(hostOnly(host), hostPort(host, 19132), 4000);
                live = r.online + "/" + r.max;
            } catch (Exception e2) {
                live = "недоступен";
            }
        }

        if (st.count == 0) {
            ctx.reply("📊 " + host + "\nТекущий онлайн: " + live
                    + "\n\nИстория ещё не собрана — статистика копится автоматически (раз в 5 мин). "
                    + "Загляни позже для min/avg/max за " + hours + " ч.");
            return;
        }
        ctx.reply("📊 Статистика: " + host + " (за " + hours + " ч)\n"
                + "Текущий онлайн: " + live + "\n"
                + "Минимум: " + st.min + "\n"
                + "Средний: " + String.format("%.1f", st.avg) + "\n"
                + "Рекорд: " + st.record + " (" + TS.format(Instant.ofEpochSecond(st.peakTs)) + ")\n"
                + "Слотов: " + st.slots + "\n"
                + "Замеров: " + st.count);
    }

    static void mcgraph(Ctx ctx) {
        String[] a = ctx.getArgLine().trim().split("\\s+");
        if (a.length == 0 || a[0].isBlank()) {
            ctx.reply("Использование: /mcgraph <хост|алиас> [часов]\nНапр.: /mcgraph mc.artygrief.su 48");
            return;
        }
        String host = ServerStore.resolve(ctx.userId(), a[0]);
        int hours = StatsStore.getHours(host);
        if (a.length > 1) {
            try {
                hours = Integer.parseInt(a[1]);
                if (hours < 1) {
                    hours = 1;
                }
                if (hours > 720) {
                    hours = 720;
                }
                StatsStore.setHours(host, hours);
            } catch (NumberFormatException ignored) {
            }
        }
        long to = System.currentTimeMillis() / 1000;
        long from = to - (long) hours * 3600;
        List<StatsStore.Sample> pts = StatsStore.samples(host, from);
        if (pts.isEmpty()) {
            ctx.reply("📈 По " + host + " ещё нет собранных данных за " + hours + " ч. "
                    + "Статистика копится автоматически — попробуй позже.\n"
                    + "(Окно графика установлено: " + hours + " ч.)");
            return;
        }
        try {
            Map<String, List<StatsStore.Sample>> series = new LinkedHashMap<>();
            series.put(host, pts);
            byte[] png = GraphRenderer.render("Онлайн: " + host + " (" + hours + " ч)", series, from, to);
            ctx.replyPhoto(png, "📈 " + host + " — онлайн за " + hours + " ч");
        } catch (Exception e) {
            ctx.reply("Не удалось построить график: " + e.getMessage());
        }
    }

    static void mccompare(Ctx ctx) {
        String[] a = ctx.getArgLine().trim().split("\\s+");
        if (a.length < 2) {
            ctx.reply("Использование: /mccompare <сервер1> <сервер2> [сервер3 ...]");
            return;
        }
        long to = System.currentTimeMillis() / 1000;
        int hours = 24;
        long from = to - (long) hours * 3600;
        Map<String, List<StatsStore.Sample>> series = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder("🆚 Сравнение (за " + hours + " ч):\n");
        boolean any = false;
        for (String token : a) {
            String host = ServerStore.resolve(ctx.userId(), token);
            List<StatsStore.Sample> pts = StatsStore.samples(host, from);
            series.put(host, pts);
            StatsStore.Stats st = StatsStore.stats(host, from);
            if (st.count > 0) {
                any = true;
                sb.append("• ").append(host).append(": avg ").append(String.format("%.1f", st.avg))
                        .append(", рекорд ").append(st.record).append("\n");
            } else {
                sb.append("• ").append(host).append(": нет данных\n");
            }
        }
        if (!any) {
            ctx.reply(sb + "\nИстория ещё не собрана — статистика копится автоматически.");
            return;
        }
        try {
            byte[] png = GraphRenderer.render("Сравнение серверов (" + hours + " ч)", series, from, to);
            ctx.replyPhoto(png, sb.toString());
        } catch (Exception e) {
            ctx.reply(sb.toString());
        }
    }

    static void mojangblacklist(Ctx ctx) {
        String arg = ctx.getArgLine().trim();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mojangblacklist <хост>");
            return;
        }
        String host = hostOnly(ServerStore.resolve(ctx.userId(), arg));
        try {
            boolean blocked = MojangBlacklist.isBlocked(host);
            ctx.reply(blocked
                    ? "🚫 " + host + " — В чёрном списке Mojang."
                    : "✅ " + host + " — НЕ в чёрном списке Mojang.");
        } catch (Exception e) {
            ctx.reply("Не удалось проверить ЧС Mojang: " + e.getMessage());
        }
    }

    static void launcherad(Ctx ctx) {
        String url = System.getenv("LAUNCHER_AD_URL");
        if (url == null || url.isBlank()) {
            ctx.reply("📢 Источник рекламируемых серверов TL/LL не настроен.\n"
                    + "Задай переменную окружения LAUNCHER_AD_URL (JSON-список вида "
                    + "[{\"name\":\"...\",\"ip\":\"...\"}]), и команда начнёт выводить список.");
            return;
        }
        try {
            String body = Utils.httpGet(url);
            JsonElement root = JsonParser.parseString(body);
            JsonArray arr;
            if (root.isJsonArray()) {
                arr = root.getAsJsonArray();
            } else if (root.isJsonObject() && root.getAsJsonObject().has("servers")) {
                arr = root.getAsJsonObject().getAsJsonArray("servers");
            } else {
                arr = new JsonArray();
            }
            if (arr.isEmpty()) {
                ctx.reply("📢 Список рекламируемых серверов пуст.");
                return;
            }
            StringBuilder sb = new StringBuilder("📢 Рекламируемые серверы (TL/LL):\n");
            int i = 1;
            for (JsonElement e : arr) {
                if (!e.isJsonObject()) {
                    continue;
                }
                JsonObject o = e.getAsJsonObject();
                String name = o.has("name") ? o.get("name").getAsString() : "?";
                String ip = o.has("ip") ? o.get("ip").getAsString()
                        : (o.has("address") ? o.get("address").getAsString() : "?");
                sb.append(i++).append(". ").append(name).append(" — ").append(ip).append("\n");
                if (i > 50) {
                    break;
                }
            }
            ctx.reply(sb.toString());
        } catch (Exception e) {
            ctx.reply("Не удалось получить список: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private static String hostOnly(String host) {
        return host.contains(":") ? host.split(":")[0] : host;
    }

    private static int hostPort(String host, int def) {
        if (host.contains(":")) {
            try {
                return Integer.parseInt(host.split(":")[1]);
            } catch (NumberFormatException ignored) {
            }
        }
        return def;
    }
}
