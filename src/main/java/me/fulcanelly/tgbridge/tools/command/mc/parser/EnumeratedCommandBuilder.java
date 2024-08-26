package me.fulcanelly.tgbridge.tools.command.mc.parser;

import static me.fulcanelly.tgbridge.tools.command.mc.parser.CommandBuilder.named;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

public class EnumeratedCommandBuilder {

    LinkedList<CommandBuilder> builders = new LinkedList<>();
    Optional<String> permission = Optional.empty();
    Consumer<ArgumentsBundle> consumer;

    public static EnumeratedCommandBuilder enumerated(String ...names) {
        //for ()
        return new EnumeratedCommandBuilder(names);
    }

    EnumeratedCommandBuilder(String ...names) {
        for (var name : names) {
            builders.add(named(name));
        }
    }

    public EnumeratedCommandBuilder setExecutor(Consumer<ArgumentsBundle> consumer) {
        this.consumer = consumer;
        return this;
    }
    
    public EnumeratedCommandBuilder setPermission(String perm) {
        permission = Optional.of(perm);
        return this;
    }

    public CommandBuilder[] done() {
        for (var builder : builders) {
            builder.setExecutor(consumer);
            if (permission.isPresent()) {
                builder.setPermission(permission.get());
            };
        }
        return builders.stream().toArray(CommandBuilder[]::new);
    }

}