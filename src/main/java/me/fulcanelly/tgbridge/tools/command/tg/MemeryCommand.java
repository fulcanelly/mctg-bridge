package me.fulcanelly.tgbridge.tools.command.tg;

import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tools.command.tg.base.StringReplierBuilder;
import me.fulcanelly.tgbridge.utils.analyst.CommonMetrix;

public class MemeryCommand extends StringReplierBuilder {

    @Inject
    public MemeryCommand(CommonMetrix metrix) {
        super("memory", metrix::getMemoryUsage);
    }
    
}
