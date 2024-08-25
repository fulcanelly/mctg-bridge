package me.fulcanelly.tgbridge.tools.hooks.invites;

import com.google.inject.Inject;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.AllArgsConstructor;
// import me.fulcanelly.insyscore.InviteSysCore;
// import me.fulcanelly.insyscore.database.InvitationsDatabase;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tools.command.tg.InvitePersonCommand;
import me.fulcanelly.tgbridge.tools.hooks.ForeignPluginHook;
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
        // InviteSysCore plugin = (InviteSysCore) prePlugin;

    }
}