package me.fulcanelly.tgbridge;

import com.google.inject.Guice;
import com.google.inject.Inject;

import org.bukkit.plugin.java.JavaPlugin;

import me.fulcanelly.tgbridge.listeners.telegram.TelegramListener;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;

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

    @Override
    public void onEnable() {
        var injector = Guice.createInjector(
            new TelegramModule(this)
        );

        injector.injectMembers(this);
    }   

}
