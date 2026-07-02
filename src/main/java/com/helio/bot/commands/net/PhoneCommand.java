package com.helio.bot.commands.net;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;

import java.util.Locale;

public class PhoneCommand implements Command {

    @Override
    public String name() {
        return "phone";
    }

    @Override
    public String description() {
        return "Информация о номере телефона.";
    }

    @Override
    public Category category() {
        return Category.NETWORK;
    }

    @Override
    public void execute(CommandContext ctx) {
        String p = ctx.argsTrimmed();
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
            String typeStr = switch (util.getNumberType(num)) {
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
            ctx.reply("Не удалось разобрать номер. Укажи в международном формате, напр. +79161234567.");
        }
    }
}
