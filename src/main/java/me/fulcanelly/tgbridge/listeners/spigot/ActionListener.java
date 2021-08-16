package me.fulcanelly.tgbridge.listeners.spigot;

import org.bukkit.event.Listener;

import org.bukkit.event.entity.PlayerDeathEvent;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.Getter;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;


import java.util.*;

import org.bukkit.event.EventHandler;

import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.compact.MessageCompactableSender;
import me.fulcanelly.tgbridge.tools.compact.context.CompactionContext;
import me.fulcanelly.tgbridge.tools.compact.context.NoteMessageCtx;
import me.fulcanelly.tgbridge.tools.compact.context.SignedMessageCtx;
import me.fulcanelly.tgbridge.tools.compact.message.CompactableMessage;
import me.fulcanelly.tgbridge.tools.compact.message.PlayerMessage;
import me.fulcanelly.tgbridge.utils.UsefulStuff;


public class ActionListener implements Listener {
    
    @Getter final MessageCompactableSender sender;

    final TGBot bot;
    final Long chatId;
    

    public ActionListener(TGBot bot, String chatId) {
        this.bot = bot;
        this.chatId = chatId == null ? null : Long.valueOf(chatId);
        this.sender = new MessageCompactableSender(bot, this.chatId);
    }


    @EventHandler
    void onPlayerJoing(PlayerJoinEvent event) {
        String player_name = event
            .getPlayer()
            .getName();
        sender.sendNote(
            String.format("`%s` join the server", player_name)
        );
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        String name = event
            .getPlayer()
            .getName();
        sender.sendNote(
            String.format("`%s` left the server", name)
        );
    }
    
    @EventHandler
    void onDeath(PlayerDeathEvent event) {
        sender.sendNote(
            UsefulStuff.formatMarkdown(
                event.getDeathMessage().replaceAll("§\\w", "")
            )
        );
    }

    //todo
    @EventHandler
    void onAchivement(PlayerAdvancementDoneEvent event) {

    }

    @EventHandler
    void onChatEvent(AsyncPlayerChatEvent event) {
        String player_name = event.getPlayer().getName();
        String message = event.getMessage();
        if (event.isCancelled()) {
            return;
        } else {
            sender.sendAsPlayer(player_name, UsefulStuff.formatMarkdown(message));
        }
    }
}