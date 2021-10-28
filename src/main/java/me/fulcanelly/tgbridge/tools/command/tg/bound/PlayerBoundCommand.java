package me.fulcanelly.tgbridge.tools.command.tg.bound;


import com.google.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Data;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;

@Data
public abstract class PlayerBoundCommand implements CommandRegister {

    @Inject
    protected SignupLoginReception reception;

    public abstract String onBoundPlayerMessage(CommandEvent event, String player);

    public abstract String getCommandName();

    public String onMessage(CommandEvent event) {
        var id = event.getFrom().getId();
        var playerName = reception.getPlayerByTg(id);

        if (playerName.isEmpty()) {
            return "Your account not bound to minecraft one";
        }

        return onBoundPlayerMessage(event, playerName.get());
    }

    @Override
    public void registerCommand(CommandManager manager) {
        new ReplierBuilder(this.getCommandName(), this::onMessage).registerCommand(manager);;
    }
    

}
