package me.fulcanelly.tgbridge;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import me.fulcanelly.clsql.databse.SQLQueryHandler;
import me.fulcanelly.tgbridge.listeners.spigot.ActionListener;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.ActualLastMessageObserver;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.MessageSender;
import me.fulcanelly.tgbridge.tools.SecretCodeMediator;
import me.fulcanelly.tgbridge.tools.TelegramLogger;
import me.fulcanelly.tgbridge.tools.command.mc.CommandProcessor;
import me.fulcanelly.tgbridge.tools.command.tg.AttachCommand;
import me.fulcanelly.tgbridge.tools.command.tg.ChatIDCommand;
import me.fulcanelly.tgbridge.tools.command.tg.KickMeCommand;
import me.fulcanelly.tgbridge.tools.command.tg.ListCommand;
import me.fulcanelly.tgbridge.tools.command.tg.MemeryCommand;
import me.fulcanelly.tgbridge.tools.command.tg.PingCommand;
import me.fulcanelly.tgbridge.tools.command.tg.StartCommand;
import me.fulcanelly.tgbridge.tools.command.tg.StatsCommand;
import me.fulcanelly.tgbridge.tools.command.tg.TopCommand;
import me.fulcanelly.tgbridge.tools.command.tg.UptimeCommand;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.compact.MessageCompactableSender;
import me.fulcanelly.tgbridge.tools.hooks.ForeignPluginHook;
import me.fulcanelly.tgbridge.tools.hooks.invites.InviteSystemFacade;
import me.fulcanelly.tgbridge.tools.hooks.loginsec.LoginSecurityFacade;
import me.fulcanelly.tgbridge.tools.mastery.ChatSettings;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.config.ConfigManager;
import me.fulcanelly.tgbridge.utils.database.SqliteConnectionProvider;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;
import me.fulcanelly.tgbridge.view.NamedTabExecutor;
import static me.fulcanelly.tgbridge.tools.command.mc.parser.CommandBuilder.*;

import me.fulcanelly.tgbridge.tools.command.mc.parser.ArgumentBuilder;
import me.fulcanelly.tgbridge.tools.command.mc.parser.CommandParser;
import me.fulcanelly.tgbridge.tools.command.mc.parser.CommandSchema;
import me.fulcanelly.tgbridge.tools.mastery.ChatSettings;

import static me.fulcanelly.tgbridge.tools.command.mc.parser.EnumeratedCommandBuilder.*;
import me.fulcanelly.tgbridge.tools.twofactor.InGameReceptionUI;
import me.fulcanelly.tgbridge.utils.events.pipe.Listener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class TelegramModule extends AbstractModule { 
    
    @Getter
    @NonNull Plugin plugin;

    @Provides @Singleton
    Connection provideConnection(SqliteConnectionProvider cp) {
        return cp.getConnection();
    }

    @Provides @Singleton
    SQLQueryHandler provideSQLhandler(Connection conn, @Named("log.sql") Boolean verbose) {
        return new SQLQueryHandler(conn, verbose);
    }

    @Provides @Singleton
    SecretCodeMediator provideSecretCodeMediator(@Named("spigot.logger") Logger logger) {
        return new SecretCodeMediator(logger);
    }
    
    @Provides @Singleton
    ConfigManager<MainConfig> provideConfig() {
        return new ConfigManager<MainConfig>(new MainConfig(), plugin);
    }

    @Provides @Singleton
    ChatSettings provideChatSettings(SQLQueryHandler sqlite) {
        return new ChatSettings(sqlite);
    }

    
    @Provides @Singleton
    MainConfig provideConfig(ConfigManager<MainConfig> configmManager) {
        configmManager.load();
        return configmManager.getConfig();
    }

    @Provides @Singleton
    TGBot provideTGBot(MainConfig config, EventPipe ePipe) {
        return new TGBot(config.getApiToken(), ePipe);
    }

    @Provides @Singleton
    TelegramLogger provideTelegramLogger(MainConfig config, TGBot bot) {
        return new TelegramLogger(config.log_status ? bot : null, config);
    }

    @Provides @Singleton @Named("bot.username")
    String getBotUsername(TGBot bot) {
        return bot.getMe()
            .getUsername();
    }
    

    @Provides @Singleton 
    CommandManager provideCommandManager(@Named("bot.username") String username) {
        return new CommandManager(username);
    }

    @Provides @Singleton 
    ActionListener provideActionListener(TGBot bot, MainConfig config, MessageSender sender, SignupLoginReception reception) {
        return new ActionListener(bot, config.getChatId(), sender, reception, config);
    }

    @Provides @Singleton 
    CommandSchema providesDefaultSchema(InGameReceptionUI reception, ChatSettings chatSettings) {

        return create()
            .setName("tg")
            .addCommand(
                named("chat")
                    .setDescription("controls telegram chat visibility")
                    .addCommand(
                        enumerated("show", "hide")
                            .setExecutor(args -> {
                                if (args.getEnumArgument().equals("show")) {
                                    chatSettings.makeChatShow(args.getSender());
                                } else {
                                    chatSettings.makeChatHide(args.getSender());
                                }
                            })
                            .done()
                    ),
                named("account")
                    .setDescription("controls telegram account")
                    .addCommand(
                        named("register")
                            .setExecutor(args -> reception.onPlayerRegisterRequest((Player)args.getSender()))
                    )
            )
            .generateHelpPage()
            .done();
    }

    @Provides @Singleton 
    ConsoleCommandSender provideConsoleCommandSender() {
        return plugin.getServer().getConsoleSender();
    }

    @Provides @Singleton
    MessageSender providSender(TGBot bot, MainConfig config) {
        return new MessageCompactableSender(bot, config.getChatId() != null ? Long.valueOf(config.getChatId()) : null);
    }

    @Provides @Singleton
    ActualLastMessageObserver provideLastMessageObvserver(MessageSender sender) {
        return (ActualLastMessageObserver)sender;
    }

    @Provides @Singleton
    Plugin providesPlugin() {
        return plugin;
    }

    @Override
    protected void configure() {
        
        
        var hookMultibind = Multibinder.newSetBinder(binder(), ForeignPluginHook.class);
       
        List.of(
            InviteSystemFacade.class,
            LoginSecurityFacade.class
        )
        .forEach(it -> hookMultibind.addBinding().to(it).in(Scopes.SINGLETON));

        var commandMultibinder = Multibinder.newSetBinder(binder(), CommandRegister.class);

        List.of(
            KickMeCommand.class,
            ListCommand.class,
            AttachCommand.class,
            ChatIDCommand.class,
            MemeryCommand.class,
            PingCommand.class,
            StartCommand.class,
            StatsCommand.class,
            TopCommand.class,
            UptimeCommand.class
        ).forEach(cmd -> commandMultibinder.addBinding().to(cmd).in(Scopes.SINGLETON));

       // bind(NamedTabExecutor.class)
      //      .to(ChatSettings.class);
        bind(TabExecutor.class)
            .to(CommandProcessor.class)
            .in(Scopes.SINGLETON);
    //    bind(MainConfig.class)
      //      .in(Scopes.SINGLETON);

        bind(StatCollector.class)
            .in(Scopes.SINGLETON);

        bind(EventPipe.class)
            .in(Scopes.SINGLETON);

        bind(SignupLoginReception.class)
            .in(Scopes.SINGLETON);

        bind(Logger.class)
            .annotatedWith(
                Names.named("spigot.logger")
            )
            .toInstance(plugin.getLogger());

        bind(SqliteConnectionProvider.class)
            .in(Scopes.SINGLETON);       
        
        bind(File.class)
            .annotatedWith(
                Names.named("plugin_folder")
            )
            .toInstance(plugin.getDataFolder());
        
        bindConstant()
            .annotatedWith(Names.named("log.sql"))
            .to(false);
    }
    
}
