package me.fulcanelly.tgbridge.listeners;

import java.util.ArrayList;
import java.util.List;

import me.fulcanelly.tgbridge.TgBridge;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.utils.events.pipe.EventHandler;
import me.fulcanelly.tgbridge.utils.events.pipe.Listener;

class Template {    
    public static final String dflt_strt = 
        ChatColor.BLUE + "[tg]" +
        ChatColor.YELLOW + "[%s]";
    
    public static final String dflt_end = 
        ChatColor.RESET + " %s";


    public static final String unkn_strt =
        ChatColor.BLUE + "[tg]" +
        ChatColor.YELLOW + "[%s]";
    
    public static final String unkn_end =
        ChatColor.RESET + " sent something";

}

public class TelegramListener implements Listener {
    TgBridge bridge;

    TextComponent formatMessage(Message msg) {
        String text = msg.getText();
        TextComponent result = new TextComponent();

        String begin = null;
        String ending = null;
        
        String name = msg.getFrom().getName();

        //begining
        if(text == null) {
            begin = String.format(Template.unkn_strt, name);
            ending = Template.unkn_end;
        } else {
            begin = String.format(Template.dflt_strt, name);
            ending = String.format(Template.dflt_end, text);
        }

        result.addExtra(begin);

        Message reply = msg.getReplyTo();

        //reply mark
        if(!reply.is_null()) {
            TextComponent replyComponent = formatMessage(reply); 
            TextComponent сomponent = new TextComponent(ChatColor.GRAY + "(in reply to)");

            HoverEvent hEvent = new HoverEvent( 
                HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(replyComponent).create()
            );

            сomponent.setHoverEvent(hEvent);
            result.addExtra(сomponent);
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

    List<Message> messagesBuffer = new ArrayList<>();

    @EventHandler
    public void onMessage(MessageEvent event) {
        String text = event.getText();
        TextComponent message = null;

        if(text != null && text.startsWith("/")) {
            bridge.tgpipe.emit(new CommandEvent(event.msg));
        } else {
            message = formatMessage(event);
            broadcast(message);
        }
    }

    @EventHandler
    public void onCommand(CommandEvent event) {
        bridge.commandManager.tryMatch(event);
    }
}

