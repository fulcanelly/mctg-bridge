package me.fulcanelly.tgbridge.tools.command;

import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tools.command.base.StringReplierBuilder;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;

public class TopCommand extends StringReplierBuilder {

    @Inject
    public TopCommand(StatCollector scollector) {
        super("top", scollector::getMessage);
    }
    
}
