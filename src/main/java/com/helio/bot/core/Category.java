package com.helio.bot.core;

/** Логические группы команд — используются для красивого вывода в /help. */
public enum Category {
    INFO("ℹ\uFE0F Информация"),
    TEXT("✍\uFE0F Текст"),
    IP("\uD83C\uDF10 IP"),
    NETWORK("\uD83D\uDCE1 Сеть"),
    MEDIA("\uD83D\uDDBC Медиа"),
    MINECRAFT("⛏ Minecraft");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
