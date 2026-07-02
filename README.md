# helio-pasta-nafig 🌵

Telegram-бот с набором утилит на **Java 17 + Lombok** с чистой ООП-архитектурой команд.

## Архитектура

```
com.helio.bot
├── Main                 — точка входа
├── HelioBot             — транспортный слой (long polling), делегирует в реестр
├── core
│   ├── Command          — контракт команды (name/description/category/aliases/execute)
│   ├── Category         — группы команд для /help
│   ├── CommandContext   — доступ к сообщению/аргументам + reply-хелперы
│   ├── CommandRegistry  — реестр, резолв по имени/алиасам, группировка
│   └── BotBootstrap     — композиционный корень (ручной DI)
├── commands
│   ├── info | text | ip | net | media | minecraft   — по классу на команду
├── minecraft            — доменный слой (пинг, репозитории, сбор статистики)
├── render               — QuoteRenderer, GraphRenderer
├── text                 — TextTransforms (чистые преобразования)
└── util                 — HttpService, Whois
```

Каждая команда — отдельный класс, реализующий `Command`; зависимости (HTTP, репозитории,
сервисы) внедряются через конструктор (Lombok `@RequiredArgsConstructor`). Добавить команду =
создать класс и зарегистрировать его в `BotBootstrap`.

## Сборка и запуск

```bash
mvn clean package
BOT_TOKEN=<токен_от_BotFather> BOT_USERNAME=<имя> java -jar target/helio-bot.jar
```

Переменные окружения: `BOT_TOKEN` (обязательно), `BOT_USERNAME`, `LAUNCHER_AD_URL` (для `/launcherad`).

## Команды

Все ~35 команд реализованы и сгруппированы в `/help` по категориям: информация, текст, IP,
сеть, медиа, Minecraft. Статистика Minecraft (`/mcstats`, `/mcgraph`, `/mccompare`) работает
на встроенном сборщике (`StatsCollector`), который раз в 5 минут пингует добавленные
(`/mcaddserver`) серверы и копит замеры онлайна в `~/.helio-bot-stats.json`.
