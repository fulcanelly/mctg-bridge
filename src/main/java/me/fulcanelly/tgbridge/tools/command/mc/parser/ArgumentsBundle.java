package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Data
public class ArgumentsBundle {

    final CommandSender sender;
    CommandSchema command;

    public String getEnumArgument() {
        return command.getName();
    }

    Map<String, Argument> parsersByName = new HashMap<>();
    Map<String, String> valuesByName = new HashMap<>();

    public Object getObject(String name) {
        var argument = parsersByName.get(name);
        var value = valuesByName.get(name);

        var result = argument.parser.apply(value);

        if (argument.isOptional() && result == null) {
            return argument.defaultSupplier.get();
        }
        return result;
    }

    public String getString(String name) {
        return (String)getObject(name);
    }

    public int getNumber(String name) {
        return Integer.valueOf(getString(name));
    }

    public boolean isPresent(String name) {
        return valuesByName.get(name) != null;
    }

    public CommandSender getSender() {
        return sender;
    }
}