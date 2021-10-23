package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.Optional;
import java.util.function.Consumer;

public class CommandBuilder {
    CommandSchema cmd = new CommandSchema();

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

    CommandBuilder addCommand(CommandSchema another) {
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

    CommandSchema done() {
        return cmd;
    }
}