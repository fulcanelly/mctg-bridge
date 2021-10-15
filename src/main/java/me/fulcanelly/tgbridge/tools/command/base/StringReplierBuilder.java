package me.fulcanelly.tgbridge.tools.command.base;

import java.util.function.Supplier;


public class StringReplierBuilder extends ReplierBuilder {

    public StringReplierBuilder(String command, Supplier<String> supplier) {
        super(command, ignoredEvent -> supplier.get());
    }
    
}
