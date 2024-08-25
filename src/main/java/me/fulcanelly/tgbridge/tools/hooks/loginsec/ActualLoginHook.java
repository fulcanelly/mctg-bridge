package me.fulcanelly.tgbridge.tools.hooks.loginsec;

import com.google.inject.Inject;
// import com.lenis0012.bukkit.loginsecurity.LoginSecurity;
// import com.lenis0012.bukkit.loginsecurity.session.AuthService;
// import com.lenis0012.bukkit.loginsecurity.session.PlayerSession;
// import com.lenis0012.bukkit.loginsecurity.session.action.ChangePassAction;
// import com.lenis0012.bukkit.loginsecurity.session.action.RemovePassAction;

import org.bukkit.plugin.Plugin;

import lombok.AllArgsConstructor;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.tools.hooks.ForeignPluginHook;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.data.LazyValue;

@AllArgsConstructor
public class ActualLoginHook {

    SignupLoginReception reception;
    Plugin basePlugin;
    CommandManager cManager;

    Object getSessionFor(String playerName) {
        return null;
    }

    String removePassword(CommandEvent event) {
        return null;

    }

    public void start() {
        registerFew(
                new ReplierBuilder("removepass", this::removePassword),
                new ReplierBuilder("changepass", this::changePassword));

    }

    void registerFew(ReplierBuilder... builders) {
        for (var builder : builders) {
            builder.registerCommand(cManager);
        }
    }

    String changePassword(CommandEvent event) {
        return null;
    }

}
