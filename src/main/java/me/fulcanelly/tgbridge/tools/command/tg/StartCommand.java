package me.fulcanelly.tgbridge.tools.command.tg;

import java.util.function.Function;

import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.tools.twofactor.BotUIReception;

public class StartCommand implements CommandRegister {

    @Inject
    BotUIReception reception;

     String onStartCommand(CommandEvent event) {
        var args = event.getArgs();
        if (args.size() == 1) {
            if (reception.onPrivateStartCommand(event.getMsgId(), args.get(0))) {
                return "ok you are signed up now";
            };
            return "something went wrong";
        } 

        return "hm?";
    }

    @Override
    public void registerCommand(CommandManager manager) {
        new ReplierBuilder("start", this::onStartCommand).registerCommand(manager);
    }
    
}
