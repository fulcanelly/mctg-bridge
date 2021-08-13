package me.fulcanelly.tgbridge;

import java.io.File;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;
import java.sql.Connection;

import org.apache.commons.lang.ObjectUtils.Null;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.fulcanelly.clsql.databse.SQLQueryHandler;
import me.fulcanelly.tgbridge.utils.database.SqliteConnectionProvider;

@Data 
public class TelegramModule extends AbstractModule { 

    JavaPlugin plugin;

    @Provides
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
            .toInstance(new File("dasdas"));// plugin.getDataFolder());
    
    }
    
}

}
