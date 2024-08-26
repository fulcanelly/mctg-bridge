package me.fulcanelly.tgbridge.tools.command.tg.bound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.fulcanelly.tgbridge.tapi.events.CommandEvent;

public abstract class OnlinePlayerBoundCommand extends PlayerBoundCommand {
    
    public abstract String onBoundOnlinePlayerMessage(CommandEvent event, Player player);

    @Override
    public String onBoundPlayerMessage(CommandEvent event, String playerName) {
        var player = Bukkit.getPlayer(playerName); 
        if (player == null) {
            return "You are not online";
        }
        return onBoundOnlinePlayerMessage(event, player);
    }


}
