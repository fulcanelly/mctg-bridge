package me.fulcanelly.tgbridge.listeners;

import me.fulcanelly.tgbridge.TgBridge;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.utils.events.pipe.EventHandler;
import me.fulcanelly.tgbridge.utils.events.pipe.Listener;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class TelegramListener implements Listener {
    static class Template {
        public static final String defBeginning = ChatColor.BLUE + "[tg]" + ChatColor.YELLOW + "[%s]";
        public static final String defEnding = ChatColor.RESET + " %s";
        public static final String unknownBeginning = ChatColor.BLUE + "* [tg]" + ChatColor.YELLOW + "[%s]";
        public static final String unknownEnding = ChatColor.GRAY + " sent something";
        //todo:  public static final String message = "{unk.sign}[tg][{from}]{unk.mark} {msg.text} {text.caption}";
    }

    TgBridge bridge;

    TextComponent formatMessage(Message msg) {
        String text = msg.getText();
        TextComponent result = new TextComponent();

        String beginning;
        String ending;

        String name = msg.getFrom().getName();
        //String caption = msg.getCaption();
        //begining
        if (text == null) {
            beginning = String.format(Template.unknownBeginning, name);
            ending = Template.unknownEnding;
            String caption = msg.getCaption();
            if (caption != null) {
                ending += " with caption: " + ChatColor.RESET + caption;
            }
       
        } else {
            beginning = String.format(Template.defBeginning, name);
            ending = String.format(Template.defEnding, text);
        }

        result.addExtra(beginning);

        Message reply = msg.getReplyTo();

        //reply mark
        if (!reply.is_null()) {
            TextComponent replyComponent = formatMessage(reply);
            TextComponent component = new TextComponent(ChatColor.GRAY + "(in reply to)");

            HoverEvent hEvent = new HoverEvent(
                HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(replyComponent).create()
            );

            component.setHoverEvent(hEvent);
            result.addExtra(component);
        }
        //ending 
        result.addExtra(ending);

        return result;
    }

    public TelegramListener(TgBridge bridge) {
        this.bridge = bridge;
    }

    void broadcast(TextComponent component) {
        Bukkit
            .spigot()
            .broadcast(component);

        ConsoleCommandSender console = bridge.getServer()
            .getConsoleSender();

        console.spigot()
            .sendMessage(component);
    }

    @EventHandler
    public void onMessage(MessageEvent event) {
        String text = event.getText();
        TextComponent message;

        if (text != null && text.startsWith("/")) {
            bridge.tgpipe.emit(new CommandEvent(event.msg));
        } else /*if(event.getChat().getId().toString() == bridge.chat)*/ {            
            message = formatMessage(event);
            broadcast(message);
        }
    }

    @EventHandler
    public void onCommand(CommandEvent event) {
        bridge.commandManager.tryMatch(event);
    }
}

