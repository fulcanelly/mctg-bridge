package me.fulcanelly.tgbridge.tools.command;

import me.fulcanelly.tgbridge.tools.command.base.PersistentStringBuilder;

public class PingCommand extends PersistentStringBuilder {

    public PingCommand() {
        super("ping", "pong");
    }
    
}
