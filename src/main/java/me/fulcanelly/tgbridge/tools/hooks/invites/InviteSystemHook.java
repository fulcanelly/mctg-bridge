package me.fulcanelly.tgbridge.tools.hooks.invites;

import com.google.inject.Inject;

import org.bukkit.plugin.Plugin;
import lombok.AllArgsConstructor;
import me.fulcanelly.insyscore.InviteSysCore;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tools.command.tg.InvitePersonCommand;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.data.LazyValue;

@AllArgsConstructor
public class InviteSystemHook {

    @Inject
    Plugin prePlugin;

    @Inject
    CommandManager cManager;

    @Inject 
    SignupLoginReception reception;

    void start() {
        InviteSysCore plugin = (InviteSysCore) prePlugin;

        new InvitePersonCommand(
            LazyValue.of(plugin::getDatabase), reception
        ).registerCommand(cManager);    }
}