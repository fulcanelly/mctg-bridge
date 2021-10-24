package me.fulcanelly.tgbridge.tools.command.tg.base;

import java.util.function.Function;

import me.fulcanelly.tgbridge.tapi.events.CommandEvent;

public class ReplierBuilder extends FullCommandBuilder {

    public ReplierBuilder(String command, Function<CommandEvent, String> eventReplier) {
        super(command, event -> event.reply(eventReplier.apply(event)));
    }

}
