package me.fulcanelly;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.File;

import javax.management.RuntimeErrorException;

import com.google.inject.Guice;

import org.bukkit.plugin.PluginBase;
import org.junit.jupiter.api.Test;

import me.fulcanelly.clsql.databse.SQLQueryHandler;
import me.fulcanelly.tgbridge.TelegramModule;


public class ModuleTest {
    @Test
    void test() {

        var jplugin = mock(PluginBase.class);
        doReturn(new File(".")).when(jplugin).getDataFolder();

        var injector = Guice.createInjector(new TelegramModule(jplugin));

        injector.getInstance(SQLQueryHandler.class).stopIt();;

    }
}