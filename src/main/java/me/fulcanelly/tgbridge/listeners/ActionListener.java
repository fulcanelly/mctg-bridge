package me.fulcanelly.tgbridge.listeners;

import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.event.entity.PlayerDeathEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.event.EventHandler;

import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.utils.UsefulStuff;

public class ActionListener implements Listener {
    final TGBot bot;
    final Long chat_id;

    private void send(String text) {
        bot.sendMessage(chat_id, text);
    }

    public ActionListener(final TGBot bot, String chat_id) {
        this.bot = bot;
        this.chat_id = Long.parseLong(chat_id);
    }

    @EventHandler
    void onPlayerJoing(PlayerJoinEvent event) {
        String player_name = event
            .getPlayer()
            .getName();
        String text = String.format(
            "`%s` join the server", 
            UsefulStuff.formatMarkdown(player_name)
        );
        send(text);
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        String name = event
            .getPlayer()
            .getName();
        String text = String.format("`%s` left the server", UsefulStuff.formatMarkdown(name));
        send(text);
    }
    
    @EventHandler
    void onDeath(PlayerDeathEvent event) {
        send(event.getDeathMessage());
    }

    @EventHandler
    void onAchivement(PlayerAdvancementDoneEvent event) {
        System.out.println(event.getAdvancement().getKey().getKey());
        //AdvancementProgress
      //  System.out.println(event.getAdvancement().getCriteria());
       // send(event.getAdvancement().toString());
    }

    @EventHandler
    void onChatEvent(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();

        playerName = UsefulStuff.formatMarkdown(playerName);
        message = UsefulStuff.formatMarkdown(message);
        
        String text = String.format("*<%s>* %s", playerName, message);
        send(text);
    }
}