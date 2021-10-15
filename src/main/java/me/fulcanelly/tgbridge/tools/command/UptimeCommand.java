package me.fulcanelly.tgbridge.tools.command;


import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tools.command.base.StringReplierBuilder;
import me.fulcanelly.tgbridge.utils.analyst.CommonMetrix;

public class UptimeCommand extends StringReplierBuilder {

    @Inject
    public UptimeCommand(CommonMetrix metrix) {
        super("uptime", metrix::getUptime);
    }
    
}
