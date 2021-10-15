package me.fulcanelly.tgbridge.listeners.telegram;

import com.google.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import lombok.AllArgsConstructor;
import lombok.Setter;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.tools.ActualLastMessageObserver;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.compact.MessageCompactableSender;
import me.fulcanelly.tgbridge.tools.mastery.ChatSettings;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;
import me.fulcanelly.tgbridge.utils.events.pipe.EventReactor;
import me.fulcanelly.tgbridge.utils.events.pipe.Listener;

import net.md_5.bungee.api.chat.TextComponent;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class TelegramListener implements Listener {

    final ConsoleCommandSender console;
    final ChatSettings chatSetting;
    final EventPipe telepipe;
    final MainConfig config;
    final ActualLastMessageObserver msgobserv;  
    final CommandManager comds;
    
    void broadcast(TextComponent component) {
        
        for (var player : Bukkit.getOnlinePlayers()) {
            chatSetting.getPlayerVisibility(player.getName())
                .andThenSilently(chatHiden -> {
                    if (!chatHiden) {
                        player.spigot().sendMessage(component);
                    }
                });
        }

        console.spigot()
            .sendMessage(component);
    }

    boolean isRightChat(MessageEvent event) {
        return event.getChat()
            .getId()
            .toString()
            .equals(
                config.getChatId());
    }

    @EventReactor
    public void onMessage(MessageEvent event) {
        var rightChat = this.isRightChat(event);

        if (rightChat) {
            msgobserv.setActualLast(event.getMsgId());
        }

        if (isCommandEvent(event)) {
            return;
        } else if (rightChat) {            
            broadcast(new EventFormatter(event).getText());
        }
    }

    boolean isCommandEvent(MessageEvent event) {
        String text = event.getText();

        if (text != null && text.startsWith("/")) {
            telepipe.emit(new CommandEvent(event));

            return true;
        }

        return false;
    }

    @EventReactor
    public void onCommand(CommandEvent event) {
        comds.tryMatch(event);
    }
}

