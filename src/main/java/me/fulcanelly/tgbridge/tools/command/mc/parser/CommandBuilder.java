package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.Optional;
import java.util.function.Consumer;

public class CommandBuilder {
    CommandSchema cmd = new CommandSchema();

    public static CommandBuilder create() {
        return new CommandBuilder();
    }

    public static CommandBuilder named(String name) {
        return create().setName(name);
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
            this.addCommand(it.done());
        }
        return this;
    }

    public CommandBuilder addCommand(CommandSchema another) {
        cmd.commandByName.put(another.name, another);
        return this;
    }

    public <T>CommandBuilder addArgument(ArgumentBuilder<T> abuilder) {
        return this.addArgument(abuilder.done());
    }

    public CommandBuilder addArgument(Argument argument) {
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

    public CommandSchema done() {
        return cmd;
    }
}