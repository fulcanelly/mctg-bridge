package me.fulcanelly.tgbridge.tools.command;

import me.fulcanelly.tgbridge.tools.command.base.StringReplierBuilder;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;

public class TopCommand extends StringReplierBuilder {

    public TopCommand(StatCollector scollector) {
        super("top", scollector::getMessage);
    }
    
}
