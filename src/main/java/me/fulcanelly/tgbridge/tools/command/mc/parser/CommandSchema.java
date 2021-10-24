package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class CommandSchema {

    String name;
    String description;

    Optional<String> permission = Optional.empty();

    Map<String, CommandSchema> commandByName = new HashMap<>();  
    Map<String, Argument> argumentByName = new HashMap<>();

    Optional<Consumer<ArgumentsBundle>> evaluator = Optional.empty();
     
    List<String> getCommandSuggestions(String name) {
        return this.commandByName.keySet().stream()
            .filter(cmdName -> cmdName.startsWith(name))
            .collect(Collectors.toList());
    }

    List<String> getArgumentSuggestions(String name) {
        return this.argumentByName.keySet().stream()
            .filter(argName -> argName.startsWith(name))
            .map(argName -> argName + ":")
            .collect(Collectors.toList());
   }

    public String getWrongArgsError() {
        return "wrong arguments error";
    }

    public String getUnknownArgError() {
        return "unknown argument: ";
    }

    public String getNotEnoughError() {
        return "not enough arguments";
    }

    public boolean isCanBeEvaluated() {
        return evaluator.isPresent();
    }

    public boolean isHaveSubcommands() {
        return commandByName.size() > 0;
    }

    public Argument getArgument(String name) {
        return argumentByName.get(name);
    }

    public CommandSchema getCommand(String name) {
        return commandByName.get(name);
    }
    
}