package me.fulcanelly.tgbridge.tools.command.tg;

import me.fulcanelly.tgbridge.tools.command.tg.base.PersistentStringBuilder;

public class PingCommand extends PersistentStringBuilder {

    public PingCommand() {
        super("ping", "pong");
    }
    
}
