# helio-pasta-nafig 🌵

Telegram-бот с набором утилит, написанный на **Java 17 + Lombok**.

## Стек
- [TelegramBots](https://github.com/rubenlagus/TelegramBots) 6.9.7.1 (long polling)
- Lombok
- Gson
- ZXing (генерация QR)
- libphonenumber (`/phone`)

## Сборка и запуск

```bash
mvn clean package
BOT_TOKEN=<токен_от_BotFather> BOT_USERNAME=<имя_бота> java -jar target/helio-bot.jar
```

Переменные окружения:
- `BOT_TOKEN` (обязательно) — токен бота от @BotFather
- `BOT_USERNAME` (опционально) — username бота
- `LAUNCHER_AD_URL` (опционально) — URL JSON-списка серверов для `/launcherad`

## Команды (все реализованы)

| Команда | Описание |
|---|---|
| /help, /bot, /aliases | Справка / информация |
| /base64 | base64 кодирование/декодирование (`-d` для декода) |
| /hash | MD5 / SHA-1 / SHA-256 |
| /genstr | Генерация случайной строки (`/genstr <длина> [hex]`) |
| /reverse /rotate /rune /smallcaps /superscript /translit | Текстовые трансформации |
| /id | ID пользователя / чата / реплая / форварда |
| /dec2ip /ipconvert | Конвертация IP (dec↔IPv4, hex/oct/bin) |
| /ipinfo | Информация по IP (ip-api.com) |
| /currency | Курс валют к рублю (ЦБ РФ) |
| /weather | Погода (open-meteo) |
| /port | Проверка TCP-порта |
| /whois | WHOIS домена/IP (IANA → referral) |
| /qrcode | Генерация QR-кода |
| /quote | Картинка-цитата |
| /phone | Информация о номере (libphonenumber): страна, тип, валидность |
| /mcping | Пинг Minecraft-сервера — Java Edition + автофолбэк на Bedrock |
| /mcaddserver /mcmyservers /mcremoveserver /mcserveralias | Управление списком серверов |
| /mcstats | min / avg / max / рекорд онлайна за окно |
| /mcgraph | График онлайна (PNG), `/mcgraph <сервер> [часов]` задаёт окно |
| /mccompare | Сравнение онлайна нескольких серверов (график + сводка) |
| /mojangblacklist | Проверка сервера в ЧС Mojang (blockedservers, SHA-1 + wildcard) |
| /launcherad | Список рекламируемых серверов TL/LL (источник в `LAUNCHER_AD_URL`) |

## Сбор статистики Minecraft

При старте бота запускается фоновый сборщик (`StatsCollector`), который раз в 5 минут
пингует все добавленные серверы (`/mcaddserver`) и пишет замеры онлайна в
`~/.helio-bot-stats.json`. На основе этих замеров работают `/mcstats`, `/mcgraph`, `/mccompare`.
История начинает накапливаться сразу после добавления сервера — первые графики появятся
через несколько интервалов сбора.
