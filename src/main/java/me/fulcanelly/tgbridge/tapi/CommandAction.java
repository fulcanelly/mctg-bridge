package me.fulcanelly.tgbridge.tapi;

import me.fulcanelly.tgbridge.tapi.events.CommandEvent;

public interface CommandAction {
	void run(CommandEvent msg);
}