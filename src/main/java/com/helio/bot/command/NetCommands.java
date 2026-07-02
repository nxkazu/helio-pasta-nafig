package com.helio.bot.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class NetCommands {
    private NetCommands() {
    }

    private static String get(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "-";
    }

    static void currency(Ctx ctx) throws Exception {
        String body = Utils.httpGet("https://www.cbr-xml-daily.ru/daily_json.js");
        JsonObject valute = JsonParser.parseString(body).getAsJsonObject().getAsJsonObject("Valute");
        String arg = ctx.getArgLine().trim().toUpperCase();
        StringBuilder sb = new StringBuilder("💱 Курс ЦБ РФ:\n");
        String[] codes = arg.isBlank() ? new String[]{"USD", "EUR", "CNY", "GBP", "JPY"} : arg.split("\\s+");
        for (String code : codes) {
            if (valute.has(code)) {
                JsonObject c = valute.getAsJsonObject(code);
                sb.append(c.get("Nominal").getAsInt()).append(" ").append(code)
                        .append(" = ").append(String.format("%.4f", c.get("Value").getAsDouble())).append(" ₽\n");
            } else {
                sb.append(code).append(": не найдено\n");
            }
        }
        if (arg.isBlank()) {
            sb.append("\nПодсказка: /currency USD EUR");
        }
        ctx.reply(sb.toString());
    }

    static void ipinfo(Ctx ctx) throws Exception {
        String ip = ctx.getArgLine().trim();
        if (ip.isBlank()) {
            ctx.reply("Использование: /ipinfo <IP>");
            return;
        }
        String body = Utils.httpGet("http://ip-api.com/json/" + URLEncoder.encode(ip, StandardCharsets.UTF_8)
                + "?fields=status,message,country,regionName,city,isp,org,as,query,timezone");
        JsonObject o = JsonParser.parseString(body).getAsJsonObject();
        if (!"success".equals(get(o, "status"))) {
            ctx.reply("Не удалось получить данные: " + get(o, "message"));
            return;
        }
        ctx.reply("🌐 " + get(o, "query") + "\n"
                + "Страна: " + get(o, "country") + "\n"
                + "Регион: " + get(o, "regionName") + "\n"
                + "Город: " + get(o, "city") + "\n"
                + "ISP: " + get(o, "isp") + "\n"
                + "Орг: " + get(o, "org") + "\n"
                + "AS: " + get(o, "as") + "\n"
                + "TZ: " + get(o, "timezone"));
    }

    static void weather(Ctx ctx) throws Exception {
        String place = ctx.getArgLine().trim();
        if (place.isBlank()) {
            ctx.reply("Использование: /weather <город>");
            return;
        }
        String geo = Utils.httpGet("https://geocoding-api.open-meteo.com/v1/search?count=1&language=ru&name="
                + URLEncoder.encode(place, StandardCharsets.UTF_8));
        JsonObject g = JsonParser.parseString(geo).getAsJsonObject();
        if (!g.has("results") || g.getAsJsonArray("results").isEmpty()) {
            ctx.reply("Место не найдено.");
            return;
        }
        JsonObject loc = g.getAsJsonArray("results").get(0).getAsJsonObject();
        double lat = loc.get("latitude").getAsDouble();
        double lon = loc.get("longitude").getAsDouble();
        String name = loc.get("name").getAsString()
                + (loc.has("country") ? ", " + loc.get("country").getAsString() : "");
        String wx = Utils.httpGet("https://api.open-meteo.com/v1/forecast?current=temperature_2m,"
                + "relative_humidity_2m,wind_speed_10m&latitude=" + lat + "&longitude=" + lon);
        JsonObject w = JsonParser.parseString(wx).getAsJsonObject().getAsJsonObject("current");
        ctx.reply("🌤 Погода: " + name + "\n"
                + "Температура: " + w.get("temperature_2m").getAsDouble() + "°C\n"
                + "Влажность: " + w.get("relative_humidity_2m").getAsInt() + "%\n"
                + "Ветер: " + w.get("wind_speed_10m").getAsDouble() + " км/ч");
    }

    static void port(Ctx ctx) {
        String[] a = ctx.getArgLine().trim().split("\\s+");
        if (a.length < 2) {
            ctx.reply("Использование: /port <хост> <порт>");
            return;
        }
        int port;
        try {
            port = Integer.parseInt(a[1]);
        } catch (NumberFormatException e) {
            ctx.reply("Некорректный порт.");
            return;
        }
        try (Socket s = new Socket()) {
            long start = System.currentTimeMillis();
            s.connect(new InetSocketAddress(a[0], port), 5000);
            long ms = System.currentTimeMillis() - start;
            ctx.reply("✅ " + a[0] + ":" + port + " (TCP) открыт — " + ms + " мс\n(UDP-проверка недоступна для connectionless-протокола)");
        } catch (Exception e) {
            ctx.reply("❌ " + a[0] + ":" + port + " (TCP) закрыт / недоступен");
        }
    }

    static void whois(Ctx ctx) throws Exception {
        String q = ctx.getArgLine().trim();
        if (q.isBlank()) {
            ctx.reply("Использование: /whois <домен|IP>");
            return;
        }
        String resp = whoisQuery("whois.iana.org", q);
        String refer = null;
        for (String line : resp.split("\n")) {
            String l = line.trim().toLowerCase();
            if (l.startsWith("refer:") || l.startsWith("whois:")) {
                refer = line.trim().substring(line.trim().indexOf(':') + 1).trim();
                break;
            }
        }
        String out = resp;
        if (refer != null && !refer.isBlank()) {
            try {
                out = whoisQuery(refer, q);
            } catch (Exception ignored) {
            }
        }
        if (out.length() > 3500) {
            out = out.substring(0, 3500) + "\n…";
        }
        ctx.reply(out.isBlank() ? "Нет данных." : out);
    }

    private static String whoisQuery(String server, String query) throws Exception {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(server, 43), 8000);
            s.setSoTimeout(8000);
            s.getOutputStream().write((query + "\r\n").getBytes(StandardCharsets.UTF_8));
            s.getOutputStream().flush();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = s.getInputStream().read(buf)) != -1) {
                bos.write(buf, 0, n);
            }
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    static void qrcode(Ctx ctx) throws Exception {
        String text = ctx.getArgLine();
        if (text.isBlank()) {
            ctx.reply("Использование: /qrcode <текст>");
            return;
        }
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 2);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512, hints);
        BufferedImage img = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bos);
        String cap = text.length() > 50 ? text.substring(0, 50) + "…" : text;
        ctx.replyPhoto(bos.toByteArray(), "QR: " + cap);
    }

    static void quote(Ctx ctx) throws Exception {
        String text = ctx.getArgLine();
        String author = null;
        if ((text == null || text.isBlank()) && ctx.getMessage().getReplyToMessage() != null
                && ctx.getMessage().getReplyToMessage().hasText()) {
            text = ctx.getMessage().getReplyToMessage().getText();
            if (ctx.getMessage().getReplyToMessage().getFrom() != null) {
                author = ctx.getMessage().getReplyToMessage().getFrom().getFirstName();
            }
        }
        if (text == null || text.isBlank()) {
            ctx.reply("Использование: /quote <текст> или ответом на сообщение");
            return;
        }
        ctx.replyPhoto(QuoteRenderer.render(text, author), null);
    }

    static void phone(Ctx ctx) {
        String p = ctx.getArgLine().trim();
        if (p.isBlank()) {
            ctx.reply("Использование: /phone <номер, напр. +79161234567>");
            return;
        }
        try {
            PhoneNumberUtil util = PhoneNumberUtil.getInstance();
            String digits = p.replaceAll("[^0-9]", "");
            String raw = p.startsWith("+") ? p : ("+" + digits);
            Phonenumber.PhoneNumber num = util.parse(raw, null);
            boolean valid = util.isValidNumber(num);
            boolean possible = util.isPossibleNumber(num);
            String region = util.getRegionCodeForNumber(num);
            String country = (region != null && !region.isEmpty())
                    ? new Locale("", region).getDisplayCountry(new Locale("ru")) : "неизвестно";
            PhoneNumberUtil.PhoneNumberType type = util.getNumberType(num);
            String typeStr = switch (type) {
                case MOBILE -> "мобильный";
                case FIXED_LINE -> "стационарный";
                case FIXED_LINE_OR_MOBILE -> "стационарный/мобильный";
                case TOLL_FREE -> "бесплатный (8-800)";
                case PREMIUM_RATE -> "премиум";
                case VOIP -> "VoIP";
                case SHARED_COST -> "с распределённой оплатой";
                case PERSONAL_NUMBER -> "персональный";
                case PAGER -> "пейджер";
                case UAN -> "UAN";
                case VOICEMAIL -> "голосовая почта";
                default -> "неизвестно";
            };
            ctx.reply("📞 Номер: " + util.format(num, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL) + "\n"
                    + "E.164: " + util.format(num, PhoneNumberUtil.PhoneNumberFormat.E164) + "\n"
                    + "Страна: " + country + (region != null ? " (" + region + ")" : "") + "\n"
                    + "Тип: " + typeStr + "\n"
                    + "Код страны: +" + num.getCountryCode() + "\n"
                    + "Валидный: " + (valid ? "да" : (possible ? "возможен, но не подтверждён" : "нет")));
        } catch (Exception e) {
            ctx.reply("Не удалось разобрать номер. Укажи в международном формате, напр. +79161234567. (" + e.getMessage() + ")");
        }
    }
}
