package me.fulcanelly.tgbridge;

import java.io.File;
import java.sql.Connection;
import java.util.logging.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import org.bukkit.plugin.Plugin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import me.fulcanelly.clsql.databse.SQLQueryHandler;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.SecretCodeMediator;
import me.fulcanelly.tgbridge.tools.mastery.ChatSettings;
import me.fulcanelly.tgbridge.utils.config.ConfigManager;
import me.fulcanelly.tgbridge.utils.database.SqliteConnectionProvider;

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

    @Override
    protected void configure() {
        
        bind(MainConfig.class)
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
