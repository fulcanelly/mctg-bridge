package me.fulcanelly.tgbridge.listeners.telegram;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import lombok.AllArgsConstructor;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.ActualLastMessageObserver;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.mastery.ChatSettings;


import net.md_5.bungee.api.chat.TextComponent;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class TelegramListener  {

    final ConsoleCommandSender console;
    final ChatSettings chatSetting;
    // final EventPipe telepipe;
    final MainConfig config;
    final ActualLastMessageObserver msgobserv;  
    final CommandManager comds;
    
    final EventBus eventBus;

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

    boolean isRightChat(Message event) {
        return event.getChat()
            .getId()
            .toString()
            .equals(
                config.getChatId());
    }

    @Subscribe
    public void onMessage(Message event) {
        var rightChat = this.isRightChat(event);

        if (rightChat) {
            msgobserv.setActualLast(event.getMsgId());
        }

        if (isCommandEvent(event)) {
            eventBus.post(new CommandEvent(event));

        } else if (rightChat && config.enable_chat) {          

            broadcast(new EventFormatter(event, config.enable_dithering).getText());
        }
    }

    @Subscribe
    public void onCommand(CommandEvent event) {
        comds.tryMatch(event);
    }

    boolean isCommandEvent(Message event) {
        String text = event.getText();

        if (text != null && text.startsWith("/")) {
            return true;
        }

        return false;
    }


}

