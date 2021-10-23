package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArgumentsBundle {

    final CommandSender sender;

    Map<String, Argument> parsersByName = new HashMap<>();
    Map<String, String> valuesByName = new HashMap<>();

    Object getObject(String name) {
        var argument = parsersByName.get(name);
        var value = valuesByName.get(name);

        var result = argument.parser.apply(value);

        if (argument.isOptional() && result == null) {
            return argument.defaultSupplier.get();
        }
        return result;
    }

    String getString(String name) {
        return (String)getObject(name);
    }

    int getNumber(String name) {
        return Integer.valueOf(getString(name));
    }

    boolean isPresent(String name) {
        return valuesByName.get(name) != null;
    }
}