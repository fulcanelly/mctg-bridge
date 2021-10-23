package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.function.Function;

import com.google.common.base.Supplier;

public class ArgumentBuilder<T> {
    
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