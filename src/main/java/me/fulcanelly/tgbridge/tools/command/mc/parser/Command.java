package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class Command {

    String name;
    String description;

    Optional<String> permission = Optional.empty();

    Map<String, Command> commandByName = new HashMap<>();  
    Map<String, Argument> argumentByName = new HashMap<>();

    Optional<Consumer<ArgumentsBundle>> evaluator = Optional.empty();
    
    public String getWrongArgsError() {
        return "wrong arguments error";
    }

    public String getUnknownArgError(String argument) {
        return "unknown argument: " + argument;
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

    public Command getCommand(String name) {
        return commandByName.get(name);
    }
    
}