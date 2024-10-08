package me.fulcanelly.tgbridge.tools.command.tg;

import com.google.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.bound.OnlinePlayerBoundCommand;

public class KickMeCommand extends OnlinePlayerBoundCommand {

    @Inject 
    Plugin plugin;

    @Override
    public String onBoundOnlinePlayerMessage(CommandEvent event, Player player) {
        Bukkit.getScheduler().runTask(plugin, it -> {
            player.kickPlayer("kick by your request");
        });
        return "You are kicked successfully";
    }

    @Override
    public String getCommandName() {
        return "kickme";
    }
    
}
