package me.fulcanelly.tgbridge.tools.command.tg;

import lombok.AllArgsConstructor;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.insyscore.database.InvitationsDatabase;

@AllArgsConstructor
public class InvitePersonCommand implements CommandRegister {

    InvitationsDatabase idb;
    SignupLoginReception reception;

    String onMessage(CommandEvent event) {
        var from = event.getFrom();
        var player = reception.getPlayerByTg(from.getId());

        if (player.isEmpty()) {
            return "Your account not bound to minecraft one";
        }

        if (event.getArgs().size() == 0) {
            return "Not enough arguments, specify nick person who you want invite to";
        }

        var toInvite = event.getArgs().get(0);

        if (idb.invite(player.get(), toInvite)) {
            return toInvite + " have invited";
        } else {
            return toInvite + " already invited";
        }
    }

    @Override
    public void registerCommand(CommandManager manager) {
        new ReplierBuilder("invite", this::onMessage).registerCommand(manager);
    }
    
}
