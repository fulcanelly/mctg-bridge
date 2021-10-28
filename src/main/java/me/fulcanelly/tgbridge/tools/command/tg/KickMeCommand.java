package me.fulcanelly.tgbridge.tools.command.tg;

import com.google.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.tools.command.tg.bound.OnlinePlayerBoundCommand;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;

public class KickMeCommand extends OnlinePlayerBoundCommand {

    @Override
    public String onBoundOnlinePlayerMessage(CommandEvent event, Player player) {
        player.kickPlayer("kick by your request");
        return "You are kicked successfully";
    }

    @Override
    public String getCommandName() {
        return "kickme";
    }
    
}
