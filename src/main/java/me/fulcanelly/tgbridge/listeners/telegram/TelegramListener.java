package me.fulcanelly.tgbridge.listeners.telegram;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.tools.mastery.ChatSettings;
import me.fulcanelly.tgbridge.utils.events.pipe.EventReactor;
import me.fulcanelly.tgbridge.utils.events.pipe.Listener;
import me.fulcanelly.tgbridge.view.MainControll;
import net.md_5.bungee.api.chat.TextComponent;

public class TelegramListener implements Listener {

    final MainControll bridge;
    final ConsoleCommandSender console;
    final ChatSettings chatSetting;
    
    public TelegramListener(MainControll bridge) {
        this.bridge = bridge;
        console = bridge.getServer()
            .getConsoleSender();
        chatSetting = bridge.getChatSettings();
    }

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
                bridge.getPinnedChatId() );
    }

    @EventReactor
    public void onMessage(MessageEvent event) {
        var rightChat = this.isRightChat(event);

        if (rightChat) {
            bridge
                .getActionListener()
                .setActualLast(event.getMsgId());
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
            bridge.getTelegramPipe().emit(new CommandEvent(event));

            return true;
        }

        return false;
    }

    @EventReactor
    public void onCommand(CommandEvent event) {
        bridge.getCommandManager().tryMatch(event);
    }
}

