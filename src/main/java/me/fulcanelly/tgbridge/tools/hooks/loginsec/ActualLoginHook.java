package me.fulcanelly.tgbridge.tools.hooks.loginsec;


import com.lenis0012.bukkit.loginsecurity.LoginSecurity;
import com.lenis0012.bukkit.loginsecurity.session.AuthService;
import com.lenis0012.bukkit.loginsecurity.session.PlayerSession;
import com.lenis0012.bukkit.loginsecurity.session.action.ChangePassAction;
import com.lenis0012.bukkit.loginsecurity.session.action.RemovePassAction;

import org.bukkit.plugin.Plugin;

import lombok.AllArgsConstructor;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;

@AllArgsConstructor
public class ActualLoginHook {

    SignupLoginReception reception;
    Plugin basePlugin;
    CommandManager cManager;

    PlayerSession getSessionFor(String playerName) {
        var player = basePlugin.getServer().getPlayer(playerName);
        var manager = LoginSecurity.getSessionManager();

        if (player == null) {
            return manager.getOfflineSession(playerName);
        }

        return manager.getPlayerSession(player);
    }

    String removePassword(CommandEvent cmd) {
        var event = cmd.getMessage();
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
     
    public void start() {
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
    

    String changePassword(CommandEvent cmd) {
        var event = cmd.getMessage();
        var player = reception.getPlayerByTg(event.getFrom().getId());

        if (player.isEmpty()) {
            return "Your account not bound to minecraft one";
        }

        if (cmd.getArgs().isEmpty()) {
            return "Not enough arguments, type new password";
        }

        var session = getSessionFor(player.get());

        var response = session.performAction(
            new ChangePassAction(AuthService.ADMIN, null, cmd.getArgs().get(0))
        );
        
        if (response.isSuccess()) {
            return "Password updated";
        }

        return response.getErrorMessage();
    }

}
