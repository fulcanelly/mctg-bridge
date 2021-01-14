package me.fulcanelly.tgbridge.view;

import org.bukkit.command.TabExecutor;

public interface NamedTabExecutor extends TabExecutor {
    String getCommandName();
}
