package me.fulcanelly.tgbridge.tools.command.tg;

import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;

public class ChatIDCommand extends ReplierBuilder {

    public ChatIDCommand() {
        super("chat_id", event -> event.getMessage().getChat().getId().toString());
    }

}
