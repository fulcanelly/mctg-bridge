package me.fulcanelly.tgbridge;

import java.io.File;
import java.sql.Connection;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import org.bukkit.plugin.Plugin;

import lombok.Data;
import lombok.NonNull;

import me.fulcanelly.clsql.databse.SQLQueryHandler;
import me.fulcanelly.tgbridge.utils.database.SqliteConnectionProvider;

@Data
public class TelegramModule extends AbstractModule { 

    @NonNull Plugin plugin;

    @Provides @Singleton
    Connection provideConnection(SqliteConnectionProvider cp) {
        return cp.getConnection();
    }

    @Provides @Singleton
    SQLQueryHandler provideSQLhandler(Connection conn, @Named("log.sql") Boolean verbose) {
        return new SQLQueryHandler(conn, verbose);
    }
    
    @Override
    protected void configure() {
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
