package me.fulcanelly.tgbridge.tools.hooks;

import com.google.inject.Inject;
import com.lenis0012.bukkit.loginsecurity.LoginSecurity;
import com.lenis0012.bukkit.loginsecurity.session.AuthService;
import com.lenis0012.bukkit.loginsecurity.session.PlayerSession;
import com.lenis0012.bukkit.loginsecurity.session.action.ChangePassAction;
import com.lenis0012.bukkit.loginsecurity.session.action.RemovePassAction;

import org.bukkit.plugin.Plugin;

import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.data.LazyValue;

public class LoginSecurityHook implements ForeignPluginHook {

    @Inject
    Plugin basePlugin;
    
    @Inject
    CommandManager cManager;

    @Inject 
    SignupLoginReception reception;

    LazyValue<Plugin> lazyPlugin = LazyValue.of(this::getPluginInstance);

    Plugin getPluginInstance() {
        return basePlugin.getServer()
            .getPluginManager().getPlugin("LoginSecurity");
    }

    @Override
    public boolean isAvailable() {
        return lazyPlugin.get() != null;
    }

    PlayerSession getSessionFor(String playerName) {
        var player = basePlugin.getServer().getPlayer(playerName);
        var manager = LoginSecurity.getSessionManager();

        if (player == null) {
            return manager.getOfflineSession(playerName);
        }

        return manager.getPlayerSession(player);
    }

    String removePassword(CommandEvent event) {
        var player = reception.getPlayerByTg(event.getFrom().getId());

        if (player.isEmpty()) {
            return "Your account not bound to minecraft one";
        }
        var session = getSessionFor(player.get());

        var response = session.performAction(
            new RemovePassAction(AuthService.ADMIN, null)
        );
        
        if (response.isSuccess()) {
            return "Password removed";
        }

        return response.getErrorMessage();
    }

    String changePassword(CommandEvent event) {
        var player = reception.getPlayerByTg(event.getFrom().getId());

        if (player.isEmpty()) {
            return "Your account not bound to minecraft one";
        }

        if (event.getArgs().isEmpty()) {
            return "Not enough arguments, type new password";
        }

        var session = getSessionFor(player.get());

        var response = session.performAction(
            new ChangePassAction(AuthService.ADMIN, null, event.getArgs().get(0))
        );
        
        if (response.isSuccess()) {
            return "Password updated";
        }

        return response.getErrorMessage();
    }

    @Override
    public void setup() {
        registerFew(
            new ReplierBuilder("removepass", this::removePassword),
            new ReplierBuilder("changepass", this::changePassword) 
        );

    }
    
    void registerFew(ReplierBuilder... builders) {
        for (var builder : builders) {
            builder.registerCommand(cManager);
        }
    }
    
}
