package me.fulcanelly.tgbridge.tools.command.base;

import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;

@AllArgsConstructor @RequiredArgsConstructor @Data
public class FullCommandBuilder implements CommandRegister {

    public @NonNull String command;
    public Consumer<CommandEvent> action;

    public void registerCommand(CommandManager manager) {
        manager.addCommand(command, action);
    }

}
