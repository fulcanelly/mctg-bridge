package me.fulcanelly.tgbridge.listeners.spigot;

import org.bukkit.event.Listener;

import org.bukkit.event.entity.PlayerDeathEvent;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;

import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.utils.UsefulStuff;


public class ActionListener implements Listener {
    public long actual_last = -1;

    public synchronized void setActualLast(long last) {
        actual_last = last;
    }

    class ShortMessage {

        String name;
        List<String> lines;
        long message_id;
        
        ShortMessage(Message msg, String from, String text) {
            setActualLast(message_id = msg.getMsgId());
            this.name = from;
            lines = new ArrayList<>();
            lines.add(text);
        }   

        ShortMessage(Message msg) {
            setActualLast(message_id = msg.getMsgId());
        }   

        String formString() {
            StringBuilder builder = new StringBuilder();
            builder.append(
                String.format("*<%s>*\n", name) );
            lines.forEach(line -> builder.append(line + "\n\n"));
            return builder.toString();
        }

        void mergeWith(String new_text) {
            lines.add(new_text);
            String for_send = formString();
            bot.editMessage(chat_id, message_id, for_send);
        }

        boolean isFrom(String another_name) {
            if (another_name == null) {
                return false;
            }
            return name.equals(another_name);
        }   

        final static long MAX_SIZE = 20;

        boolean isLimitExceeded() {
            if (lines == null) {
                return false;
            }
            return lines.size() > MAX_SIZE;
        }

        //from = null
        boolean isMergeableWith(String from) {
                        
            if (last_sended.isLimitExceeded()) {
                return false;
            } 

            return isFrom(from) && isLast();
        }

        boolean isLast() {
            return message_id == actual_last;
        }
    }

    final long MAX_SENDED_SIZE = 20;
    ShortMessage last_sended;

    final TGBot bot;
    final Long chat_id;

    void send(String text, String from) {
        
        if (chat_id == null) {
            return;
        }
        
        boolean merge = false;

        if (last_sended != null) {
            merge = last_sended.isMergeableWith(from);
        }

        if (merge) {
            last_sended.mergeWith(text);
        } else {
            String for_send = String.format("*<%s>* %s", from, text);
            Message sended = bot.sendMessage(chat_id, for_send);
            last_sended = new ShortMessage(sended, from, text);
        }
    }

    void send(String text) {
        bot.sendMessage(chat_id, text);
        last_sended = null;
    }

    
    public ActionListener(TGBot bot, String chat_id) {
        this.bot = bot;
        if (chat_id == null) {
            this.chat_id = null;
        } else {
            this.chat_id = Long.valueOf(chat_id);
        }
    }

    @EventHandler
    void onPlayerJoing(PlayerJoinEvent event) {
        String player_name = event
            .getPlayer()
            .getName();
        send(
            String.format("`%s` join the server", player_name)
        );
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        String name = event
            .getPlayer()
            .getName();
        send(
            String.format("`%s` left the server", name)
        );
    }
    
    @EventHandler
    void onDeath(PlayerDeathEvent event) {
        send(
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
            send(
                UsefulStuff.formatMarkdown(message), player_name);
        }
    }
}