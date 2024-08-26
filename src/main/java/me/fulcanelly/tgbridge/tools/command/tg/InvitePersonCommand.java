package me.fulcanelly.tgbridge.tools.command.tg;

import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.bound.PlayerBoundCommand;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.data.LazyValue;
import me.fulcanelly.insyscore.database.InvitationsDatabase;

public class InvitePersonCommand extends PlayerBoundCommand {

    public InvitePersonCommand(LazyValue<InvitationsDatabase> database, SignupLoginReception reception) {
        this.idb = database;
        this.reception = reception;
    }

    LazyValue<InvitationsDatabase> idb;

    @Override
    public String onBoundPlayerMessage(CommandEvent event, String player) {
        if (event.getArgs().size() == 0) {
            return "Not enough arguments, specify person's nick who you want invite to";
        }

        var toInvite = event.getArgs().get(0);

        if (idb.get().invite(player, toInvite)) {
            return "Successfully invited";
        } else {
            return "Already invited";
        }
    }

    @Override
    public String getCommandName() {
        return "invite";
    }
    
}
