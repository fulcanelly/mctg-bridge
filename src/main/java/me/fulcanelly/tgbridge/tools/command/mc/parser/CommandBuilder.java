package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class CommandBuilder {
    CommandSchema cmd = new CommandSchema();

    public static CommandBuilder create() {
        return new CommandBuilder();
    }

    public static CommandBuilder named(String name) {
        return create().setName(name);
    }

    // todo
    public CommandBuilder showUsageOnNoExecutor() {
        return this;
    }

    public CommandBuilder setPermission(String perm) {
        cmd.permission = Optional.of(perm);
        return this;
    }

    public CommandBuilder setDescription(String description) {
        cmd.description = description;
        return this;
    }

    public CommandBuilder setName(String name) {
        cmd.name = name;
        return this;
    }

    public CommandBuilder addCommand(CommandBuilder ...cbuilders) {
        for (var it : cbuilders) {
            this.addCommandSchema(it.done());
        }
        return this;
    }

    public CommandBuilder addCommandSchema(CommandSchema another) {
        cmd.commandByName.put(another.name, another);
        return this;
    }

    public <T>CommandBuilder addArgument(ArgumentBuilder<T> abuilder) {
        return this.addActualArgument(abuilder.done());
    }

    public CommandBuilder addActualArgument(Argument argument) {
        cmd.argumentByName.put(argument.name, argument);
        return this;
    }
    
    public CommandBuilder setExecutor(Consumer<ArgumentsBundle> executor) {
        cmd.evaluator = Optional.of(executor);
        return this;
    }
    
    public CommandBuilder generateHelpPage() {

        return this;
    }

    protected String getHelpPageMessage() {
        StringJoiner joiner = new StringJoiner("\n");
        for (var name : cmd.commandByName.keySet()) {
            var description = cmd.commandByName.get(name).getDescription();
            if (description == null) {
                description = "";
            }
            joiner.add(name + "   " + description);
        }
        return joiner.toString();

    }
    public CommandSchema done() {
        if (cmd.evaluator.isEmpty()) {
            var message = getHelpPageMessage();
            cmd.evaluator = Optional.of(a -> a.getSender().sendMessage(message));
        }
        return cmd;
    }
}