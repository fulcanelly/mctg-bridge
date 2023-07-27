package me.fulcanelly.tgbridge.tools.command.tg;

import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tools.command.tg.base.StringReplierBuilder;
import me.fulcanelly.tgbridge.tools.tunnel.TunnelManager;

public class TunnelCommand extends StringReplierBuilder {

    @Inject
    public TunnelCommand(TunnelManager handler) {
        super("tunnel", handler::normalizedUrl);
    }

}
