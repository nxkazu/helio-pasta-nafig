package com.helio.bot.command;

import java.util.List;

public final class McCommands {
    private McCommands() {
    }

    static void mcping(Ctx ctx) {
        String arg = ctx.getArgLine().trim();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mcping <хост>[:порт]");
            return;
        }
        String host = arg;
        int port = 25565;
        if (arg.contains(":")) {
            String[] p = arg.split(":");
            host = p[0];
            try {
                port = Integer.parseInt(p[1]);
            } catch (NumberFormatException ignored) {
            }
        }
        try {
            MinecraftPinger.Result r = MinecraftPinger.pingJava(host, port, 5000);
            String motd = r.motd == null ? "-" : r.motd.replaceAll("§.", "");
            ctx.reply("🟢 " + host + ":" + port + "\n"
                    + "Версия: " + (r.versionName == null ? "?" : r.versionName) + "\n"
                    + "Онлайн: " + r.online + "/" + r.max + "\n"
                    + "MOTD: " + motd + "\n"
                    + "Пинг: " + r.pingMs + " мс");
        } catch (Exception e) {
            ctx.reply("🔴 " + host + ":" + port + " — недоступен (Java Edition).\n"
                    + "Bedrock-пинг в разработке (beta).");
        }
    }

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

    static void mcstats(Ctx ctx) {
        ctx.reply("📊 Статистика сервера требует периодического сбора онлайна (планировщик + БД).\n"
                + "Текущий онлайн доступен через /mcping. Полноценная статистика/график — в разработке.");
    }

    static void mcgraph(Ctx ctx) {
        ctx.reply("📈 Настройки графика доступны после включения сбора статистики (планировщик + БД). В разработке.");
    }

    static void mccompare(Ctx ctx) {
        ctx.reply("🆚 Сравнение статистики серверов — ᵇᵉᵗᵃ. Требует собранной истории онлайна. В разработке.");
    }

    static void mojangblacklist(Ctx ctx) {
        String arg = ctx.getArgLine().trim();
        if (arg.isBlank()) {
            ctx.reply("Использование: /mojangblacklist <хост>");
            return;
        }
        ctx.reply("🚫 Проверка сервера в ЧС Mojang (blockedservers, sha1) — в разработке.");
    }

    static void launcherad(Ctx ctx) {
        ctx.reply("📢 Рекламируемые серверы в TL/LL:\n\n(Источник данных TitanLauncher/LegacyLauncher пока не подключён — команда в разработке.)");
    }
}
