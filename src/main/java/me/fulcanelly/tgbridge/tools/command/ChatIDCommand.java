package me.fulcanelly.tgbridge.tools.command;

import me.fulcanelly.tgbridge.tools.command.base.ReplierBuilder;

public class ChatIDCommand extends ReplierBuilder {

    public ChatIDCommand() {
        super("chat_id", event -> event.getChat().getId().toString());
    }

}
