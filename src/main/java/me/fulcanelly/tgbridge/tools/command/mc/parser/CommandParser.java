package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


import org.bukkit.command.CommandSender;

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

    ParseResult handleArgument(String name) {
        var clearName = name.substring(0, name.length() - 1);
        var parser = current.getArgument(clearName);
        
        if (parser == null) {
                
            return ParseResult.expected(
                current.getArgumentSuggestions(clearName),
                current.getUnknownArgError(), clearName
            );

        }

        var argument = input.poll();

        if (argument == null) {
            return ParseResult.expected(
                parser.getExpected(),
                current.getNotEnoughError()
            );
        }

        
        args.parsersByName.put(clearName, parser);
        args.valuesByName.put(clearName, argument);
        
        return ParseResult.expected(
            parser.getCompletions(argument)
        );
    }

    ParseResult handleCommand(String name) {
        var next = current.getCommand(name);
        
        if (next == null) {
            return ParseResult.expected(
                current.getCommandSuggestions(name),
                "unkonwn subcomand", name
            );
        }
        current = next;

        return ParseResult.expected(
            current.commandByName.keySet().stream()
                .collect(Collectors.toList())
        );
    }

    ParseResult handleThing(String text) {
        if (text.endsWith(":")) {
            return handleArgument(text);
        } else {
            return handleCommand(text);
        }
    }


    List<String> getLackingArguments() {
        var needed = args.valuesByName;
        var currentArgs = current.argumentByName;

        return currentArgs.keySet().stream()
            .filter(name -> !needed.containsKey(name))
            .filter(name -> currentArgs.get(name).required)
            .map(name -> name + ":")
            .collect(Collectors.toList());

    }

    public void tryRun() {
        if (current.isCanBeEvaluated()) {
            current.evaluator.get().accept(args);
        } else {
            throw new RuntimeException("this command can't be evaluated");
            //todo - generate usage 
        }
    }

    //todo
    //note: make them highlighted
    ParseResult getOptionalArguments() {
        return null;
    }
    

    public ParseResult parse() {
        ParseResult reuslt = ParseResult.getEmpty();

        while (!inputEmpty()) {
            reuslt = handleThing(input.poll());
        }

        args.setCommand(current);

        if (reuslt.isNotEmpty()) {
            return reuslt;
        }
        
        var lackingArgs = getLackingArguments();
        
        if (lackingArgs.size() > 0) {
            return ParseResult.expected(
                lackingArgs, 
                "arguments lacks"
            );
        }

        return ParseResult.expected(
            current.commandByName.keySet().stream()
                .collect(Collectors.toList())
        );
    }

    public void evaluate() {
        /*
        while (!inputEmpty()) {
            handleThing(input.poll());
        }
        
        args.setCommand(current);

        var lackingArgs = getLackingArguments();
        
        if (lackingArgs.size() > 0) {
            throw new RuntimeException("arguments lacks: " + lackingArgs.toString());
        }*/
        parse();
        tryRun();
        
    }

}