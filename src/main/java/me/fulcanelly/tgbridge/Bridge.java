package me.fulcanelly.tgbridge;

import java.util.Set;

import com.google.inject.Guice;
import com.google.inject.Inject;

import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.fulcanelly.tgbridge.listeners.spigot.ActionListener;
import me.fulcanelly.tgbridge.listeners.telegram.TelegramListener;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.SecretCodeMediator;
import me.fulcanelly.tgbridge.tools.TelegramLogger;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;
import me.fulcanelly.tgbridge.view.NamedTabExecutor;

public class Bridge extends JavaPlugin {
    
    @Override
    public void onDisable() {

    }

    @Inject 
    TelegramLogger tlog;

    @Inject
    TGBot bot;

    @Inject
    void setupTelegramBotListeners(TGBot bot, EventPipe tgpipe, TelegramListener listener) {
        bot.getDetectorManager()
            .addDetector(MessageEvent.detector);

        tgpipe
            .registerListener(listener);
    }

    @Inject 
    void generateSecretTempCode(SecretCodeMediator secode) {
        secode.generateSecretTempCode();
    }

    @Inject 
    void checkConfig(MainConfig config) {
        if (config.getChatId() == null) {
            this.getLogger().warning("chat_id is null, use /attach <secretTempCode> to pin one");
        }
    }

    void regSpigotListeners(Listener ...listeners) {
        for (var listener : listeners) {
            this.getServer()
                .getPluginManager()
                .registerEvents(listener, this);
        }

    }

    @Inject
    void regCommands(CommandManager manager, Set<CommandRegister> registers) {
        for (var register : registers) {
            register.registerCommand(manager);
        }
    } 

    @Inject 
    void registerTabExecutor(TabExecutor executor) {
        var cmd = this.getCommand("tg");
        cmd.setTabCompleter(executor);
        cmd.setExecutor(executor);
    }

    @Override
    public void onEnable() {
        try {
            System.out.println("Loading stuff...");
            var injector = Guice.createInjector(
                new TelegramModule(this)
            );
            System.out.println("Applying stuff");

            injector.injectMembers(this);
            System.out.println("Starting");

            regSpigotListeners(
                injector.getInstance(StatCollector.class), 
                injector.getInstance(ActionListener.class)
            );

            tlog.sendToPinnedChat("plugin started");
            bot.start();
        } catch(Exception e) {
           e.printStackTrace();
            System.out.println( e.getMessage());

        }

        System.out.println("done");

    }   

}
