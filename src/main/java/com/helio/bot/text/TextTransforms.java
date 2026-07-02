package com.helio.bot.text;

import java.util.HashMap;
import java.util.Map;

/** Чистые функции текстовых преобразований (руны, smallcaps, superscript, транслит, rotate). */
public final class TextTransforms {
    private TextTransforms() {
    }

    private static final String LATIN = "abcdefghijklmnopqrstuvwxyz";
    private static final Map<Character, String> RUNE = pair(LATIN, "ᚨᛒᚲᛞᛖᚠᚷᚺᛁᛃᚲᛚᛗᚾᛟᛈᚲᚱᛊᛏᚢᚹᚹᛪᛁᛉ");
    private static final Map<Character, String> SMALLCAPS = pair(LATIN, "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ");
    private static final Map<Character, String> SUPERSCRIPT = pair(LATIN, "ᵃᵇᶜᵈᵉᶠᵍʰⁱʲᵏˡᵐⁿᵒᵖqʳˢᵗᵘᵛʷˣʸᶻ");
    private static final Map<Character, String> TRANSLIT = new HashMap<>();
    private static final Map<Character, String> ROTATE = new HashMap<>();

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
            ROTATE.put(from.charAt(i), String.valueOf(to.charAt(i)));
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

    private static String apply(String s, Map<Character, String> m, boolean caseAware) {
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

    public static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    public static String rotate(String s) {
        return new StringBuilder(apply(s.toLowerCase(), ROTATE, false)).reverse().toString();
    }

    public static String rune(String s) {
        return apply(s, RUNE, true);
    }

    public static String smallcaps(String s) {
        return apply(s, SMALLCAPS, true);
    }

    public static String superscript(String s) {
        return apply(s, SUPERSCRIPT, true);
    }

    public static String translit(String s) {
        return apply(s, TRANSLIT, true);
    }
}
