package tgbridge.listeners;

import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.event.entity.PlayerDeathEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.EventHandler;

import tgbridge.tapi.TGBot;

public class ActionListener implements Listener {
    final TGBot bot;
    final Long chat_id;

    void send(String text) {
        bot.sendMessage(chat_id, text);
    }

    public ActionListener(final TGBot bot, String chat_id) {
        this.bot = bot;
        this.chat_id = Long.parseLong(chat_id);
    }

    @EventHandler
    void onPlayerJoing(PlayerJoinEvent event) {
        String text = String.format(
            "*%s* join the server", 
            event
            .getPlayer()
            .getName()
        );
        send(text);
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        String name = event
            .getPlayer()
            .getName();
        String text = String.format("*%s* left the server", name);
        send(text);
    }
    
    @EventHandler
    void onDeath(PlayerDeathEvent event) {
        send(event.getDeathMessage());
    }

    @EventHandler
    void onAchivement(PlayerAdvancementDoneEvent event) {
       // send(event.getAdvancement().toString());
    }

    @EventHandler
    void onChatEvent(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();
        String text = String.format("*<%s>* %s", playerName, message);
        send(text);
    }
}