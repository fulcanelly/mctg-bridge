package me.fulcanelly.tgbridge.tools.command.base;


public class PersistentStringBuilder extends StringReplierBuilder {

    public PersistentStringBuilder(String command, String string) {
        super(command, string::toString);
    }
    
}
