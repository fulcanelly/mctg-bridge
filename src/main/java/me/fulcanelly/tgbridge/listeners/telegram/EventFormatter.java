package me.fulcanelly.tgbridge.listeners.telegram;

import java.io.ByteArrayOutputStream;

import org.bukkit.ChatColor;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.fulcanelly.tgbridge.tapi.Message;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import java.awt.image.BufferedImage;

@RequiredArgsConstructor
public class EventFormatter {

    final Message message;
    final Boolean enableDithering;

    PhotoFormatter imger = new PhotoFormatter(100);

    static class Template {
        public static final String defBeginning = ChatColor.BLUE + "[tg]" + ChatColor.YELLOW + "[%s]";
        public static final String defEnding = ChatColor.RESET + " %s";
        public static final String unknownBeginning = ChatColor.BLUE + "* [tg]" + ChatColor.YELLOW + "[%s]";
        public static final String unknownEnding = ChatColor.GRAY + " sent something";
        //todo:  public static final String message = "{unk.sign}[tg][{from}]{unk.mark} {msg.text} {text.caption}";
    }

    @SneakyThrows
    String photoToText(BufferedImage img) {
        return imger.imageToBraille(img)
            .render(new ByteArrayOutputStream())
            .toString();
    }

    @SneakyThrows
    TextComponent formatMessage(Message msg) {
        TextComponent result = new TextComponent();
        
        String text = msg.getText();
        String name = msg.getFrom().getName();
        
        String beginning = null;
        String ending = null;

        if (enableDithering) {
            msg.getPhoto().stream()
                .map(arr -> arr.get(0).load(msg.getBot()))
                .map(this::photoToText)
                .forEach(result::addExtra);
        }
        
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


