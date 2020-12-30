package me.fulcanelly.tgbridge.view;

import org.bukkit.Server;

import me.fulcanelly.tgbridge.listeners.spigot.ActionListener;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;

public interface MainControll {
    Server getServer();
    String getPinnedChatId();
    ActionListener getActionListener();
    EventPipe getTelegramPipe();
    CommandManager getCommandManager();
}
