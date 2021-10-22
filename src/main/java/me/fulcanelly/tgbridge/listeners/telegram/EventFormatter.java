package me.fulcanelly.tgbridge.listeners.telegram;

import org.bukkit.ChatColor;

import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class EventFormatter {

    final Message message;

    public EventFormatter(MessageEvent event) {
        this.message = event;
    } 
    
    static class Template {
        public static final String defBeginning = ChatColor.BLUE + "[tg]" + ChatColor.YELLOW + "[%s]";
        public static final String defEnding = ChatColor.RESET + " %s";
        public static final String unknownBeginning = ChatColor.BLUE + "* [tg]" + ChatColor.YELLOW + "[%s]";
        public static final String unknownEnding = ChatColor.GRAY + " sent something";
        //todo:  public static final String message = "{unk.sign}[tg][{from}]{unk.mark} {msg.text} {text.caption}";
    }
    
    TextComponent formatMessage(Message msg) {
        TextComponent result = new TextComponent();
        
        String text = msg.getText();
        String name = msg.getFrom().getName();
        
        String beginning = null;
        String ending = null;

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
        
        if (!reply.is_null()) {
            TextComponent replyComponent = formatMessage(reply);
            TextComponent component = new TextComponent(ChatColor.GRAY + "(in reply to)");

            BaseComponent[] baseComponent = new ComponentBuilder(replyComponent).create();
            
            HoverEvent hevent = new HoverEvent(
                HoverEvent.Action.SHOW_TEXT, new Text(baseComponent)
            );
                
            component.setHoverEvent(hevent);
            result.addExtra(component);
        }

        result.addExtra(ending);

        return result;
    }


    public TextComponent getText() {    
        return formatMessage(message);
    }
}
