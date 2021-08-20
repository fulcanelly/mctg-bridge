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
import org.bukkit.plugin.Plugin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import me.fulcanelly.clsql.databse.SQLQueryHandler;
import me.fulcanelly.tgbridge.listeners.spigot.ActionListener;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.MessageSender;
import me.fulcanelly.tgbridge.tools.SecretCodeMediator;
import me.fulcanelly.tgbridge.tools.TelegramLogger;
import me.fulcanelly.tgbridge.tools.command.AttachCommand;
import me.fulcanelly.tgbridge.tools.command.ChatIDCommand;
import me.fulcanelly.tgbridge.tools.command.ListCommand;
import me.fulcanelly.tgbridge.tools.command.MemeryCommand;
import me.fulcanelly.tgbridge.tools.command.PingCommand;
import me.fulcanelly.tgbridge.tools.command.StatsCommand;
import me.fulcanelly.tgbridge.tools.command.TopCommand;
import me.fulcanelly.tgbridge.tools.command.UptimeCommand;
import me.fulcanelly.tgbridge.tools.command.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.compact.MessageCompactableSender;
import me.fulcanelly.tgbridge.tools.mastery.ChatSettings;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;
import me.fulcanelly.tgbridge.utils.config.ConfigManager;
import me.fulcanelly.tgbridge.utils.database.SqliteConnectionProvider;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;
import me.fulcanelly.tgbridge.view.NamedTabExecutor;

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
    ConfigManager<MainConfig> provideConfig(MainConfig config) {
        return new ConfigManager<MainConfig>(config, plugin);
    }

    @Provides @Singleton
    ChatSettings provideChatSettings(SQLQueryHandler sqlite) {
        return new ChatSettings(sqlite);
    }

    @Provides @Singleton
    StatCollector provideStatCollector(SQLQueryHandler sqlite){
        return new StatCollector(sqlite);
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
    ActionListener provideActionListener(TGBot bot, MainConfig config, MessageSender sender) {
        return new ActionListener(bot, config.getChatId(), sender);
    }


    @Provides @Singleton 
    ConsoleCommandSender provideConsoleCommandSender() {
        return plugin.getServer().getConsoleSender();
    }

    @Provides @Singleton
    MessageSender providSender(TGBot bot, MainConfig config) {
        return new MessageCompactableSender(bot, Long.valueOf(config.getChatId()));
    }

    @Override
    protected void configure() {
        
        var commandMultibinder = Multibinder.newSetBinder(binder(), CommandRegister.class);

        List.of(
            ListCommand.class,
            AttachCommand.class,
            ChatIDCommand.class,
            MemeryCommand.class,
            PingCommand.class,
            StatsCommand.class,
            TopCommand.class,
            UptimeCommand.class
        ).forEach(cmd -> commandMultibinder.addBinding().to(cmd).in(Scopes.SINGLETON));


        bind(NamedTabExecutor.class)
            .to(ChatSettings.class);

        bind(MainConfig.class)
            .in(Scopes.SINGLETON);

        bind(EventPipe.class)
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
