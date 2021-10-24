package me.fulcanelly.tgbridge.tools.command.tg;

import com.google.inject.Inject;

import lombok.AllArgsConstructor;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.SecretCodeMediator;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.utils.config.ConfigManager;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class AttachCommand implements CommandRegister {

    SecretCodeMediator secode;
    ConfigManager<MainConfig> cmanager;

    String handleEvent(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            return "Sepcify secret code";
        } else if (secode.isSecretCodeMatch(Integer.valueOf(event.args[0]))) {
            cmanager.getConfig().setChatId(event.getChat().getId());
            cmanager.save();
            secode.generateSecretTempCode();
            return "OK, done. Reload plugin";
        } else {
            return "Wrong code";
        }
    }

    @Override
    public void registerCommand(CommandManager manager) {
        new ReplierBuilder("attach", this::handleEvent).registerCommand(manager);   
    }

}
