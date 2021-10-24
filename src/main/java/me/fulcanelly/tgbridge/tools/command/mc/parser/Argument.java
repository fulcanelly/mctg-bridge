package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.base.Supplier;

import lombok.ToString;

@ToString
public class Argument {

    String name; 
    boolean required = true;
    Supplier<Object> defaultSupplier;

    Optional<String> permission = Optional.empty();
    Function<String, Object> parser = a -> a;

    //Function<Optional<String>, List<String>> expectedSupplier;
    
    List<String> getExpected() {
        return List.of();
    }

    boolean isOptional() {
        return !required;
    }
}