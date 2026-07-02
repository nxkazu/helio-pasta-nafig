package com.helio.bot.command;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Commands {
    private Commands() {
    }

    private static final String LATIN = "abcdefghijklmnopqrstuvwxyz";

    private static final Map<Character, String> ROTATE = pair(
            LATIN + "0123456789",
            "ɐqɔpǝɟƃɥıɾʞlɯuodbɹsʇnʌʍxʎz0Ɩᄅampersand".replace("ampersand", "") + "" );
    // ROTATE rebuilt properly below in static block to avoid confusion.

    private static final Map<Character, String> RUNE = pair(LATIN,
            "ᚨᛒᚲᛞᛖᚠᚷᚺᛁᛃᚲᛚᛗᚾᛟᛈᚲᚱᛊᛏᚢᚹᚹᛪᛁᛉ");
    private static final Map<Character, String> SMALLCAPS = pair(LATIN,
            "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ");
    private static final Map<Character, String> SUPERSCRIPT = pair(LATIN,
            "ᵃᵇᶜᵈᵉᶠᵍʰⁱʲᵏˡᵐⁿᵒᵖqʳˢᵗᵘᵛʷˣʸᶻ");
    private static final Map<Character, String> TRANSLIT = new HashMap<>();
    private static final Map<Character, String> ROTATE_MAP = new HashMap<>();

    static {
        String[][] tr = {
                {"а", "a"}, {"б", "b"}, {"в", "v"}, {"г", "g"}, {"д", "d"}, {"е", "e"},
                {"ё", "yo"}, {"ж", "zh"}, {"з", "z"}, {"и", "i"}, {"й", "y"}, {"к", "k"},
                {"л", "l"}, {"м", "m"}, {"н", "n"}, {"о", "o"}, {"п", "p"}, {"р", "r"},
                {"с", "s"}, {"т", "t"}, {"у", "u"}, {"ф", "f"}, {"х", "kh"}, {"ц", "ts"},
                {"ч", "ch"}, {"ш", "sh"}, {"щ", "shch"}, {"ъ", ""}, {"ы", "y"}, {"ь", ""},
                {"э", "e"}, {"ю", "yu"}, {"я", "ya"}
        };
        for (String[] e : tr) {
            TRANSLIT.put(e[0].charAt(0), e[1]);
        }
        String from = LATIN + "0123456789";
        String to = "ɐqɔpǝɟƃɥıɾʞlɯuodbɹsʇnʌʍxʎz0Ɩᄅ" + "Ɛㄣϛ9ㄥ86";
        for (int i = 0; i < from.length() && i < to.length(); i++) {
            ROTATE_MAP.put(from.charAt(i), String.valueOf(to.charAt(i)));
        }
    }

    private static Map<Character, String> pair(String from, String to) {
        Map<Character, String> m = new HashMap<>();
        int n = Math.min(from.length(), to.length());
        for (int i = 0; i < n; i++) {
            m.put(from.charAt(i), String.valueOf(to.charAt(i)));
        }
        return m;
    }

    private static String applyMap(String s, Map<Character, String> m, boolean caseAware) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            char key = caseAware ? Character.toLowerCase(c) : c;
            String rep = m.get(key);
            if (rep == null) {
                sb.append(c);
                continue;
            }
            if (caseAware && Character.isUpperCase(c) && !rep.isEmpty()) {
                rep = Character.toUpperCase(rep.charAt(0)) + rep.substring(1);
            }
            sb.append(rep);
        }
        return sb.toString();
    }

    public static Map<String, CommandDef> all() {
        LinkedHashMap<String, CommandDef> m = new LinkedHashMap<>();
        m.put("aliases", new CommandDef("Алиасы для команд.", Commands::aliases));
        m.put("base64", new CommandDef("Конвертация текста в base64 и обратно.", Commands::base64));
        m.put("bot", new CommandDef("Информация о боте.", Commands::bot));
        m.put("currency", new CommandDef("Курс валют относительно рубля (ЦБР).", NetCommands::currency));
        m.put("dec2ip", new CommandDef("Конвертация IP из десятичного в IPv4.", Commands::dec2ip));
        m.put("genstr", new CommandDef("Генерация строки.", Commands::genstr));
        m.put("hash", new CommandDef("Хеширование текста.", Commands::hash));
        m.put("help", new CommandDef("Список команд.", Commands::help));
        m.put("id", new CommandDef("Получить ID пользователя / сообщества / бота.", Commands::id));
        m.put("ipconvert", new CommandDef("Конвертация IPv4 в различные форматы.", Commands::ipconvert));
        m.put("ipinfo", new CommandDef("Информация об IP.", NetCommands::ipinfo));
        m.put("launcherad", new CommandDef("Список рекламируемых серверов в TL/LL.", McCommands::launcherad));
        m.put("mcaddserver", new CommandDef("Добавление сервера Minecraft (Java, Bedrock) в сбор статистики.", McCommands::mcaddserver));
        m.put("mccompare", new CommandDef("Сравнить статистику серверов Minecraft (Java, Bedrock). ᵇᵉᵗᵃ", McCommands::mccompare));
        m.put("mcgraph", new CommandDef("Изменение настроек графика сервера Minecraft.", McCommands::mcgraph));
        m.put("mcmyservers", new CommandDef("Вывод списка добавленных серверов Minecraft.", McCommands::mcmyservers));
        m.put("mcping", new CommandDef("Информация о сервере Minecraft (Java, Bedrock).", McCommands::mcping));
        m.put("mcremoveserver", new CommandDef("Удаление сервера Minecraft из сбора статистики.", McCommands::mcremoveserver));
        m.put("mcserveralias", new CommandDef("Установка алиаса для добавленного сервера Minecraft.", McCommands::mcserveralias));
        m.put("mcstats", new CommandDef("Получение статистики сервера Minecraft (Java, Bedrock).", McCommands::mcstats));
        m.put("mojangblacklist", new CommandDef("Наличие сервера Minecraft в ЧС Mojang.", McCommands::mojangblacklist));
        m.put("phone", new CommandDef("Информация о номере телефона.", NetCommands::phone));
        m.put("port", new CommandDef("Проверка TCP/UDP порта на доступность.", NetCommands::port));
        m.put("qrcode", new CommandDef("Генерация QR-кода из текста.", NetCommands::qrcode));
        m.put("quote", new CommandDef("Генерация изображения цитаты.", NetCommands::quote));
        m.put("reverse", new CommandDef("Разворот текста (вба).", Commands::reverse));
        m.put("rotate", new CommandDef("Переворот текста (ɐƍʚ).", Commands::rotate));
        m.put("rune", new CommandDef("Конвертация текста в руны (ᚨᛒᚹ).", Commands::rune));
        m.put("smallcaps", new CommandDef("Конвертация латинских символов в маленькие прописные (ᴀʙᴄ).", Commands::smallcaps));
        m.put("superscript", new CommandDef("Конвертация латинских символов в мелкие верхние (ᵃᵇᶜ).", Commands::superscript));
        m.put("translit", new CommandDef("Транслитерация текста (abv).", Commands::translit));
        m.put("weather", new CommandDef("Погода в указанной точке.", NetCommands::weather));
        m.put("whois", new CommandDef("Найти WHOIS информацию адреса или домена.", NetCommands::whois));
        return m;
    }

    // -------------------------------------------------------------------
    // Info / help
    // -------------------------------------------------------------------

    static void help(Ctx ctx) {
        StringBuilder sb = new StringBuilder("🌵 Команды бота:\n\n");
        for (Map.Entry<String, CommandDef> e : ctx.getBot().getCommands().entrySet()) {
            sb.append("/").append(e.getKey()).append(" — ").append(e.getValue().description()).append("\n");
        }
        ctx.reply(sb.toString());
    }

    static void bot(Ctx ctx) {
        ctx.reply("🌵 Helio Pasta Nafig Bot\n"
                + "Версия: 1.0.0\n"
                + "Автор: @nxkazu\n"
                + "Команд: " + ctx.getBot().getCommands().size() + "\n"
                + "Написан на Java 17 + Lombok.\n"
                + "/help — список команд.");
    }

    static void aliases(Ctx ctx) {
        ctx.reply("Алиасы команд пока не настроены — используйте полные имена команд.\n/help — список команд.");
    }

    // -------------------------------------------------------------------
    // Text transforms & encoding
    // -------------------------------------------------------------------

    static void base64(Ctx ctx) {
        String arg = ctx.getArgLine();
        if (arg == null || arg.isBlank()) {
            ctx.reply("Использование: /base64 <текст>  |  /base64 -d <base64>");
            return;
        }
        if (arg.startsWith("-d ")) {
            String data = arg.substring(3).trim();
            try {
                byte[] dec = Base64.getDecoder().decode(data);
                ctx.reply(new String(dec, StandardCharsets.UTF_8));
            } catch (IllegalArgumentException e) {
                ctx.reply("Некорректный base64.");
            }
        } else {
            ctx.reply(Base64.getEncoder().encodeToString(arg.getBytes(StandardCharsets.UTF_8)));
        }
    }

    static void hash(Ctx ctx) throws Exception {
        String arg = ctx.getArgLine();
        if (arg.isBlank()) {
            ctx.reply("Использование: /hash <текст>");
            return;
        }
        ctx.reply("MD5: " + Utils.digest("MD5", arg) + "\n"
                + "SHA-1: " + Utils.digest("SHA-1", arg) + "\n"
                + "SHA-256: " + Utils.digest("SHA-256", arg));
    }

    static void genstr(Ctx ctx) {
        String line = ctx.getArgLine().trim();
        String[] a = line.isEmpty() ? new String[0] : line.split("\\s+");
        int len = 16;
        if (a.length > 0) {
            try {
                len = Integer.parseInt(a[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        if (len < 1) {
            len = 1;
        }
        if (len > 4096) {
            len = 4096;
        }
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        if (a.length > 1 && a[1].equalsIgnoreCase("hex")) {
            charset = "0123456789abcdef";
        }
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(charset.charAt(rnd.nextInt(charset.length())));
        }
        ctx.reply(sb.toString());
    }

    static void reverse(Ctx ctx) {
        if (ctx.getArgLine().isBlank()) {
            ctx.reply("Использование: /reverse <текст>");
            return;
        }
        ctx.reply(new StringBuilder(ctx.getArgLine()).reverse().toString());
    }

    static void rotate(Ctx ctx) {
        if (ctx.getArgLine().isBlank()) {
            ctx.reply("Использование: /rotate <текст>");
            return;
        }
        String mapped = applyMap(ctx.getArgLine().toLowerCase(), ROTATE_MAP, false);
        ctx.reply(new StringBuilder(mapped).reverse().toString());
    }

    static void rune(Ctx ctx) {
        if (ctx.getArgLine().isBlank()) {
            ctx.reply("Использование: /rune <текст>");
            return;
        }
        ctx.reply(applyMap(ctx.getArgLine(), RUNE, true));
    }

    static void smallcaps(Ctx ctx) {
        if (ctx.getArgLine().isBlank()) {
            ctx.reply("Использование: /smallcaps <текст>");
            return;
        }
        ctx.reply(applyMap(ctx.getArgLine(), SMALLCAPS, true));
    }

    static void superscript(Ctx ctx) {
        if (ctx.getArgLine().isBlank()) {
            ctx.reply("Использование: /superscript <текст>");
            return;
        }
        ctx.reply(applyMap(ctx.getArgLine(), SUPERSCRIPT, true));
    }

    static void translit(Ctx ctx) {
        if (ctx.getArgLine().isBlank()) {
            ctx.reply("Использование: /translit <текст>");
            return;
        }
        ctx.reply(applyMap(ctx.getArgLine(), TRANSLIT, true));
    }

    // -------------------------------------------------------------------
    // IP helpers & id
    // -------------------------------------------------------------------

    static void dec2ip(Ctx ctx) {
        String arg = ctx.getArgLine().trim();
        if (arg.isBlank()) {
            ctx.reply("Использование: /dec2ip <число>");
            return;
        }
        try {
            long v = Long.parseLong(arg);
            if (v < 0 || v > 4294967295L) {
                ctx.reply("Число вне диапазона IPv4 (0..4294967295).");
                return;
            }
            String ip = ((v >> 24) & 0xFF) + "." + ((v >> 16) & 0xFF) + "." + ((v >> 8) & 0xFF) + "." + (v & 0xFF);
            ctx.reply(arg + " → " + ip);
        } catch (NumberFormatException e) {
            ctx.reply("Некорректное число.");
        }
    }

    static void ipconvert(Ctx ctx) {
        String arg = ctx.getArgLine().trim();
        String[] parts = arg.split("\\.");
        if (parts.length != 4) {
            ctx.reply("Использование: /ipconvert <IPv4>");
            return;
        }
        try {
            long v = 0;
            for (String p : parts) {
                int o = Integer.parseInt(p);
                if (o < 0 || o > 255) {
                    throw new NumberFormatException();
                }
                v = (v << 8) | o;
            }
            ctx.reply("IP: " + arg + "\n"
                    + "DEC: " + v + "\n"
                    + "HEX: 0x" + Long.toHexString(v).toUpperCase() + "\n"
                    + "OCT: 0" + Long.toOctalString(v) + "\n"
                    + "BIN: " + Long.toBinaryString(v));
        } catch (NumberFormatException e) {
            ctx.reply("Некорректный IPv4.");
        }
    }

    static void id(Ctx ctx) {
        Message m = ctx.getMessage();
        StringBuilder sb = new StringBuilder();
        if (m.getFrom() != null) {
            sb.append("Твой ID: ").append(m.getFrom().getId()).append("\n");
        }
        sb.append("Chat ID: ").append(m.getChatId());
        if (m.getReplyToMessage() != null && m.getReplyToMessage().getFrom() != null) {
            sb.append("\nID (ответ): ").append(m.getReplyToMessage().getFrom().getId());
        }
        if (m.getForwardFrom() != null) {
            sb.append("\nID (форвард): ").append(m.getForwardFrom().getId());
        }
        ctx.reply(sb.toString());
    }
}
