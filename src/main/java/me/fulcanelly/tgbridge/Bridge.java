package me.fulcanelly.tgbridge;

import java.util.Set;



import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.fulcanelly.tgbridge.listeners.spigot.ActionListener;
import me.fulcanelly.tgbridge.listeners.telegram.TelegramListener;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.SecretCodeMediator;
import me.fulcanelly.tgbridge.tools.TelegramLogger;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.hooks.ForeignPluginHook;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;

@Getter
public class Bridge extends JavaPlugin {

    @Override
    public void onDisable() {

    }

    @Inject
    TelegramLogger tlog;

    @Inject
    TGBot bot;

    @Inject
    MainConfig mainConfig;

    Injector injector;

    @Inject
    void setupTelegramBotListeners(EventBus bus, TelegramListener listener) {
        bus.register(listener);
    }

    @Inject
    void generateSecretTempCode(SecretCodeMediator secode) {
        secode.generateSecretTempCode();
    }

    boolean canStartBot = true;

    void checkToken(String apiToken) {
        if (apiToken == null || apiToken.isEmpty()) {
            this.getLogger().warning("API token is empty, try set it");
            return;
        }

        var bot = new TGBot(apiToken, null, getLogger());

        try {
            bot.getMe();
        } catch (Exception e) {
            canStartBot = false;
        }
    }

    @Inject
    void checkConfig(MainConfig config) {
        if (config.getChatId() == null) {
            this.getLogger().warning("chat_id is null, use /attach <secretTempCode> to pin one");
        }

        checkToken(config.getApiToken());

    }

    void regSpigotListeners(Listener... listeners) {
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
    void regHook(Set<ForeignPluginHook> hooks) {
        for (var hook : hooks) {
            if (hook.isAvailable()) {
                hook.setup();
            }
            ;
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
        var logger = getLogger();
        try {

            logger.info("Loading stuff...");
            injector = Guice.createInjector(
                    new TelegramModule(this));
            logger.info("Injecting stuff");

            injector.injectMembers(this);
            logger.info("Starting");

            regSpigotListeners(
                    injector.getInstance(StatCollector.class),
                    injector.getInstance(ActionListener.class));

            tlog.sendToPinnedChat("Plugin started");
            if (canStartBot) {
                bot.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(e.getMessage());
        }

        logger.info("Done");

    }

}
