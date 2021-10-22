package me.fulcanelly.tgbridge.tools.mastery;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.management.RuntimeErrorException;

import com.google.common.base.Supplier;

import org.bukkit.command.CommandExecutor;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Builder.Default;
import me.fulcanelly.tgbridge.view.NamedTabExecutor;

/**
 * init: cmd args
 * init: cmds subcmd args
 * 
 * cmd: WORD
 * 
 * subcmd: cmd
 *       | cmd | subcmd
 * 
 * arg: WORD:WORD
 *    | WORD:NUMBER
 *  
 * args:
 *     | WORD 
 *     | arg
 *     | arg args
 *  
 * 
 * 
 * ExecutorCommandBuilder builder = new CommandBuilder("tg")
 *      .addSubCmd(
 *          new CommandBuilder("chat")
 *              .addSubCmd(
 *                  new CommandBuilder("show")
 *                      .setReactor(...)
 *              )
 *              .addSubCmd(
*                   new CommandBuilder("hide")
 *                      .setReactor(...)  
 *              )
 *      )
 *
* ExecutorCommandBuilder builder = new CommandBuilder("tg")
*      .addSubCmd(
*          new CommandBuilder("chat")
*              .addArg("show")
*              .addArg("hide")
*              .setReactor(...)
*      )
*/


class Argument {

    boolean requied = true;
    Supplier<Object> defaultSupplier = () -> null;

    Optional<String> permission = Optional.empty();
    Function<String, Object> parser = a -> a;

};

@RequiredArgsConstructor
class ArgumentsBundle {

    final CommandExecutor executor;

    Map<String, Argument> parsersByName = new HashMap<>();
    Map<String, String> valuesByName = new HashMap<>();

    Object getObject(String name) {
        var argument = parsersByName.get(name);
        var value = valuesByName.get(name);

        var result = argument.parser.apply(value);

        if (!argument.requied && result == null) {
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


@Builder
class Command {

    String name;
    Optional<String> permission = Optional.empty();

    Map<String, Command> commandByName = new HashMap<>();  
    Map<String, Argument> argumentByName = new HashMap<>();

    Optional<Consumer<ArgumentsBundle>> evalutaor;
    
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
        return evalutaor.isPresent();
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


//todo
//* redo parsing by adding parsing result holding error or/and resulting command
//* add check if enough arguments

class CommandParser {

    final LinkedList<String> input;
    Command current;
    ArgumentsBundle args;

    public CommandParser(LinkedList<String> input, CommandExecutor executor) {
        this.input = input;
        this.args = new ArgumentsBundle(executor);
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
            throw new RuntimeException("unkonwn subcomand");
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

    boolean isEnoughArguments() {
        return true;
    }

    void evaluate() {
        while (current.isHaveSubcommands()) {
            handleThing(input.poll());
        }
        
        if (current.isCanBeEvaluated() && inputEmpty()) {
            current.evalutaor.get().accept(args);
        }

    }
}