package com.helio.bot.commands.net;

import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;

import java.net.InetSocketAddress;
import java.net.Socket;

public class PortCommand implements Command {

    @Override
    public String name() {
        return "port";
    }

    @Override
    public String description() {
        return "Проверка TCP/UDP порта на доступность.";
    }

    @Override
    public Category category() {
        return Category.NETWORK;
    }

    @Override
    public void execute(CommandContext ctx) {
        String[] a = ctx.argList();
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
            ctx.reply("✅ " + a[0] + ":" + port + " (TCP) открыт — " + ms + " мс\n"
                    + "(UDP-проверка недоступна для connectionless-протокола)");
        } catch (Exception e) {
            ctx.reply("❌ " + a[0] + ":" + port + " (TCP) закрыт / недоступен");
        }
    }
}
