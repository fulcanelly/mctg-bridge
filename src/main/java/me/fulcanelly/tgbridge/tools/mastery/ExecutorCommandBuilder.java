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
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import com.google.common.base.Supplier;

import org.bukkit.command.CommandExecutor;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
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

@ToString
class Argument {

    String name; 
    boolean required = true;
    Supplier<Object> defaultSupplier;

    Optional<String> permission = Optional.empty();
    Function<String, Object> parser = a -> a;

    boolean isOptional() {
        return !required;
    }
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

class Command {

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

class ArgumentBuilder<T> {
    
    Argument argument = new Argument();

    static <T>ArgumentBuilder<T> create() {
        return new ArgumentBuilder<T>();
    }

    ArgumentBuilder<T> makeOptional() {
        argument.required = false;
        return this;
    }

    ArgumentBuilder<T> setName(String name) {
        argument.name = name;
        return this;
    }

    @SuppressWarnings("unchecked")
    ArgumentBuilder<T> setDefaultSupplier(Supplier<T> supplier) {
        argument.defaultSupplier = (Supplier<Object>) supplier;
        return this;
    }
    
    @SuppressWarnings("unchecked")
    ArgumentBuilder<T> setParser(Function<String, T> parser) {
        argument.parser = (Function<String, Object>) parser;
        return this;
    }
    
    Argument done() {
        return argument;
    }

}

class CommandBuilder {
    Command cmd = new Command();

    static CommandBuilder create() {
        return new CommandBuilder();
    }

    static CommandBuilder named(String name) {
        return create().setName(name);
    }

    CommandBuilder setPermission(String perm) {
        cmd.permission = Optional.of(perm);
        return this;
    }

    CommandBuilder setDescription(String description) {
        cmd.description = description;
        return this;
    }

    CommandBuilder setName(String name) {
        cmd.name = name;
        return this;
    }

    CommandBuilder addCommand(CommandBuilder ...cbuilders) {
        for (var it : cbuilders) {
            this.addCommand(it.done());
        }
        return this;
    }

    CommandBuilder addCommand(Command another) {
        cmd.commandByName.put(another.name, another);
        return this;
    }


    <T>CommandBuilder addArgument(ArgumentBuilder<T> abuilder) {
        return this.addArgument(abuilder.done());
    }

    CommandBuilder addArgument(Argument argument) {
        cmd.argumentByName.put(argument.name, argument);
        return this;
    }
    
    CommandBuilder setExecutor(Consumer<ArgumentsBundle> executor) {
        cmd.evaluator = Optional.of(executor);
        return this;
    }
    
    CommandBuilder generateHelpPage() {
        return this;
    }

    Command done() {
        return cmd;
    }
}

//todo
//* redo parsing by adding parsing result holding error or/and resulting command
//* add check if enough arguments

class CommandParser {

    final LinkedList<String> input;
    Command current;
    ArgumentsBundle args;

    public CommandParser(Command schema, LinkedList<String> input, CommandExecutor executor) {
        this.current = schema;
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