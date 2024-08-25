package me.fulcanelly.tgbridge.tools.command.tg;

import lombok.AllArgsConstructor;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.tools.command.tg.bound.PlayerBoundCommand;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.data.LazyValue;

public class InvitePersonCommand extends PlayerBoundCommand {

    public InvitePersonCommand(LazyValue<Object> database, SignupLoginReception reception) {
        this.idb = database;
        this.reception = reception;
    }

    LazyValue<Object> idb;

    @Override
    public String onBoundPlayerMessage(CommandEvent event, String player) {
        if (event.getArgs().size() == 0) {
            return "Not enough arguments, specify person's nick who you want invite to";
        }

        var toInvite = event.getArgs().get(0);
        throw new RuntimeException();

        // if (idb.get().invite(player, toInvite)) {
        //     return "Successfully invited";
        // } else {
        //     return "Already invited";
        // }
    }

    @Override
    public String getCommandName() {
        return "invite";
    }
    
}
