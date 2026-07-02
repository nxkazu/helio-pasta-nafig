package com.helio.bot.core;

import com.helio.bot.commands.info.AliasesCommand;
import com.helio.bot.commands.info.BotInfoCommand;
import com.helio.bot.commands.info.HelpCommand;
import com.helio.bot.commands.info.IdCommand;
import com.helio.bot.commands.ip.Dec2IpCommand;
import com.helio.bot.commands.ip.IpConvertCommand;
import com.helio.bot.commands.media.QrCodeCommand;
import com.helio.bot.commands.media.QuoteCommand;
import com.helio.bot.commands.minecraft.LauncherAdCommand;
import com.helio.bot.commands.minecraft.McAddServerCommand;
import com.helio.bot.commands.minecraft.McCompareCommand;
import com.helio.bot.commands.minecraft.McGraphCommand;
import com.helio.bot.commands.minecraft.McMyServersCommand;
import com.helio.bot.commands.minecraft.McPingCommand;
import com.helio.bot.commands.minecraft.McRemoveServerCommand;
import com.helio.bot.commands.minecraft.McServerAliasCommand;
import com.helio.bot.commands.minecraft.McStatsCommand;
import com.helio.bot.commands.minecraft.MojangBlacklistCommand;
import com.helio.bot.commands.net.CurrencyCommand;
import com.helio.bot.commands.net.IpInfoCommand;
import com.helio.bot.commands.net.PhoneCommand;
import com.helio.bot.commands.net.PortCommand;
import com.helio.bot.commands.net.WeatherCommand;
import com.helio.bot.commands.net.WhoisCommand;
import com.helio.bot.commands.text.Base64Command;
import com.helio.bot.commands.text.GenStrCommand;
import com.helio.bot.commands.text.HashCommand;
import com.helio.bot.commands.text.ReverseCommand;
import com.helio.bot.commands.text.RotateCommand;
import com.helio.bot.commands.text.RuneCommand;
import com.helio.bot.commands.text.SmallCapsCommand;
import com.helio.bot.commands.text.SuperscriptCommand;
import com.helio.bot.commands.text.TranslitCommand;
import com.helio.bot.minecraft.MinecraftService;
import com.helio.bot.minecraft.MojangBlacklistService;
import com.helio.bot.minecraft.ServerRepository;
import com.helio.bot.minecraft.StatsCollector;
import com.helio.bot.minecraft.StatsRepository;
import com.helio.bot.util.HttpService;

/** Ручной композиционный корень (DI): создаёт единственные экземпляры сервисов и регистрирует все команды. */
public class BotBootstrap {

    private final HttpService http = new HttpService();
    private final ServerRepository servers = new ServerRepository();
    private final StatsRepository stats = new StatsRepository();
    private final MinecraftService minecraft = new MinecraftService();
    private final MojangBlacklistService mojang = new MojangBlacklistService(http);
    private final StatsCollector statsCollector = new StatsCollector(servers, stats, minecraft);
    private final CommandRegistry registry = new CommandRegistry();

    public BotBootstrap() {
        registerAll();
    }

    public CommandRegistry registry() {
        return registry;
    }

    public StatsCollector statsCollector() {
        return statsCollector;
    }

    private void registerAll() {
        // info
        registry.register(new BotInfoCommand(registry));
        registry.register(new HelpCommand(registry));
        registry.register(new AliasesCommand(registry));
        registry.register(new IdCommand());
        // text
        registry.register(new Base64Command());
        registry.register(new HashCommand());
        registry.register(new GenStrCommand());
        registry.register(new ReverseCommand());
        registry.register(new RotateCommand());
        registry.register(new RuneCommand());
        registry.register(new SmallCapsCommand());
        registry.register(new SuperscriptCommand());
        registry.register(new TranslitCommand());
        // ip
        registry.register(new Dec2IpCommand());
        registry.register(new IpConvertCommand());
        // net
        registry.register(new IpInfoCommand(http));
        registry.register(new CurrencyCommand(http));
        registry.register(new WeatherCommand(http));
        registry.register(new PortCommand());
        registry.register(new WhoisCommand());
        registry.register(new PhoneCommand());
        // media
        registry.register(new QrCodeCommand());
        registry.register(new QuoteCommand());
        // minecraft
        registry.register(new McPingCommand(minecraft));
        registry.register(new McAddServerCommand(servers));
        registry.register(new McMyServersCommand(servers));
        registry.register(new McRemoveServerCommand(servers));
        registry.register(new McServerAliasCommand(servers));
        registry.register(new McStatsCommand(servers, stats, minecraft));
        registry.register(new McGraphCommand(servers, stats));
        registry.register(new McCompareCommand(servers, stats));
        registry.register(new MojangBlacklistCommand(servers, mojang));
        registry.register(new LauncherAdCommand(http));
    }
}
