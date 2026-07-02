# helio-pasta-nafig 🌵

Telegram-бот с набором утилит, написанный на **Java 17 + Lombok**.

## Стек
- [TelegramBots](https://github.com/rubenlagus/TelegramBots) 6.9.7.1 (long polling)
- Lombok
- Gson
- ZXing (генерация QR)

## Сборка и запуск

```bash
mvn clean package
BOT_TOKEN=<токен_от_BotFather> BOT_USERNAME=<имя_бота> java -jar target/helio-bot.jar
```

Переменные окружения:
- `BOT_TOKEN` (обязательно) — токен бота от @BotFather
- `BOT_USERNAME` (опционально) — username бота

## Команды

| Команда | Статус | Описание |
|---|---|---|
| /help, /bot, /aliases | ✅ | Справка / информация |
| /base64 | ✅ | Кодирование/декодирование base64 (`-d` для декода) |
| /hash | ✅ | MD5 / SHA-1 / SHA-256 |
| /genstr | ✅ | Генерация случайной строки |
| /reverse /rotate /rune /smallcaps /superscript /translit | ✅ | Текстовые трансформации |
| /id | ✅ | ID пользователя / чата / реплая |
| /dec2ip /ipconvert | ✅ | Конвертация IP |
| /ipinfo | ✅ | Инфо по IP (ip-api.com) |
| /currency | ✅ | Курс валют к рублю (ЦБ РФ) |
| /weather | ✅ | Погода (open-meteo) |
| /port | ✅ | Проверка TCP-порта |
| /whois | ✅ | WHOIS домена/IP |
| /qrcode | ✅ | Генерация QR-кода |
| /quote | ✅ | Картинка-цитата |
| /mcping | ✅ | Пинг Minecraft-сервера (Java Edition, Server List Ping) |
| /mcaddserver /mcmyservers /mcremoveserver /mcserveralias | ✅ | Локальное хранилище серверов |
| /phone | 🟡 | Базовое определение страны по коду (полная валидация — TODO libphonenumber) |
| /mcstats /mcgraph /mccompare /mojangblacklist /launcherad | 🟡 | Требуют сбора статистики (планировщик + БД) / внешних источников — заглушки с TODO |

> 🟡 Заглушки написаны как валидные команды с понятным сообщением и точкой расширения. Bedrock-пинг и историческая статистика (график онлайна как на исходном скрине) — следующий шаг: добавить периодический опрос и БД.
