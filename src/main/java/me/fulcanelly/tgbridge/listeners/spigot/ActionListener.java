package me.fulcanelly.tgbridge.listeners.spigot;

import org.bukkit.event.Listener;

import org.bukkit.event.entity.PlayerDeathEvent;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.Getter;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.stream.Collectors;

import com.google.inject.Inject;

import java.util.*;

import org.bukkit.event.EventHandler;

import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.MessageSender;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.UsefulStuff;

public class ActionListener implements Listener {

    @Getter
    final MessageSender sender;

    final TGBot bot;
    final Long chatId;
    final SignupLoginReception reception;
    final MainConfig config;

    @Inject
    public ActionListener(
            TGBot bot,
            String chatId,
            MessageSender sender,
            SignupLoginReception reception,
            MainConfig config) {
        this.bot = bot;
        this.chatId = chatId == null ? null : Long.valueOf(chatId);
        this.sender = sender;
        this.reception = reception;
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoing(PlayerJoinEvent event) {
        String player_name = event
                .getPlayer()
                .getName();
        sender.sendNote(
                String.format("`%s` joined the server", player_name));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        String name = event
                .getPlayer()
                .getName();
        sender.sendNote(
                String.format("`%s` left the server", name));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        sender.sendNote(
                UsefulStuff.formatMarkdown(
                        event.getDeathMessage().replaceAll("§\\w", "")));
    }

    // todo
    @EventHandler
    public void onAchivement(PlayerAdvancementDoneEvent event) {

    }

    // forward personal messages to direct messages
    // in telegram if account is bound
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        var list = new ArrayList<String>(
                List.of(event.getMessage().split(" ")));

        if (list.size() < 3) {
            return;
        }
        if (!list.get(0).equals("/w")) {
            return;
        }

        var msg = list.subList(2, list.size())
                .stream().collect(Collectors.joining(" "));

        reception.getTgByUser(list.get(1)).stream()
                .forEach(target -> bot.sendMessage(target, UsefulStuff
                        .formatMarkdown("private message from " + event.getPlayer().getName() + ":\n\n" + msg)));
    }

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        if (!config.enable_chat) {
            return;
        }

        String player_name = event.getPlayer().getName();
        String message = event.getMessage();

        if (event.isCancelled()) {
            return;
        } else {
            sender.sendAsPlayer(player_name, UsefulStuff.formatMarkdown(message));
        }
    }
}
