package me.fulcanelly.tgbridge;

import com.google.inject.Guice;
import com.google.inject.Inject;

import org.bukkit.plugin.java.JavaPlugin;

import me.fulcanelly.tgbridge.listeners.telegram.TelegramListener;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;
import me.fulcanelly.tgbridge.view.NamedTabExecutor;

public class Bridge extends JavaPlugin {
    
    @Override
    public void onDisable() {

    }

    @Inject
    void setupTelegramBotListeners(TGBot bot, EventPipe tgpipe, TelegramListener listener) {
        bot.getDetectorManager()
            .addDetector(MessageEvent.detector);

        tgpipe
            .registerListener(listener);
    }

    @Inject 
    void checkConfig(MainConfig config) {
        if (config.getChatId() == null) {
            this.getLogger().warning("chat_id is null, use /attach <secretTempCode> to pin one");
        }
    }

    @Inject 
    void registerTabExecutor(NamedTabExecutor executor) {
        var cmd = this.getCommand(executor.getCommandName());
        cmd.setTabCompleter(executor);
        cmd.setExecutor(executor);
    }

    @Override
    public void onEnable() {
        var injector = Guice.createInjector(
            new TelegramModule(this)
        );

        injector.injectMembers(this);
    }   

}
