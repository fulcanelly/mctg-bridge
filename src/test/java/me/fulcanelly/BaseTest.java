package me.fulcanelly;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

import org.junit.jupiter.api.BeforeEach;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import me.fulcanelly.tgbridge.TelegramModule;

public class BaseTest extends AbstractModule {

    Plugin makePluginMock() {
        var pluginMock = mock(Plugin.class);

        when(pluginMock.getLogger()).thenReturn(mock(Logger.class));
        when(pluginMock.getDataFolder()).thenReturn(new File("./"));

        return pluginMock;
    }

    Module getModule() {
        return Modules.override(
                new TelegramModule(makePluginMock())).with(new BaseMocksModule());
    }

    protected Injector getInjector() {
        return Guice.createInjector(getModule());
    }

    @BeforeEach
    void setupInjector() {
        System.out.println("Injection");

        var injector = getInjector();
        injector.injectMembers(this);
    }
}
