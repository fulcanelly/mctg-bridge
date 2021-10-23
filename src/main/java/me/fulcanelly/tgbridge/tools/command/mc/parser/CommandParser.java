package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import lombok.Builder;
import lombok.Builder.Default;
import me.fulcanelly.tgbridge.view.NamedTabExecutor;



//todo
//* redo parsing by adding parsing result holding error or/and resulting command

public class CommandParser {

    final LinkedList<String> input;
    CommandSchema current;
    ArgumentsBundle args;

    public CommandParser(CommandSchema schema, LinkedList<String> input, CommandSender sender) {
        this.current = schema;
        this.input = input;
        this.args = new ArgumentsBundle(sender);
    }

    boolean inputEmpty() {
        return input.size() == 0;
    }

    void handleArgument(String name) {
        name = name.substring(0, name.length() - 1);
        var parser = current.getArgument(name);
        if (parser == null) {
            throw new RuntimeException(current.getUnknownArgError(name));
        }

        var argument = input.poll();
        if (argument == null) {
            throw new RuntimeException(current.getNotEnoughError());
        }

        args.parsersByName.put(name, parser);
        args.valuesByName.put(name, argument);

    }

    void handleCommand(String name) {
        var next = current.getCommand(name);
        
        if (next == null) {
            throw new RuntimeException("unkonwn subcomand: " + name);
        }
        current = next;
    }

    void handleThing(String text) {
        if (text.endsWith(":")) {
            handleArgument(text);
        } else {
            handleCommand(text);
        }
    }

    List<String> getLackingArguments() {
        var needed = args.valuesByName;
        var currentArgs = current.argumentByName;

        return currentArgs.keySet().stream()
            .filter(name -> !needed.containsKey(name))
            .filter(name -> currentArgs.get(name).required)
            .collect(Collectors.toList());

    }

    void tryRun() {
        if (current.isCanBeEvaluated()) {
            current.evaluator.get().accept(args);
        } else {
            throw new RuntimeException("this command can't be evaluated");
            //todo - generate usage 
        }
    }

    void evaluate() {
        while (!inputEmpty()) {
            handleThing(input.poll());
        }
        
        var lackingArgs = getLackingArguments();
        
        if (lackingArgs.size() > 0) {
            throw new RuntimeException("arguments lacks: " + lackingArgs.toString());
        }

        tryRun();
        
    }

}