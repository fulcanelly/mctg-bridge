package me.fulcanelly.tgbridge.tapi.events;

import me.fulcanelly.tgbridge.utils.events.pipe.EventObject;
import me.fulcanelly.tgbridge.tapi.Message;

public class CommandEvent extends Message implements EventObject {

    public String [] args;
    public <T>CommandEvent(T msg) {
        super(msg);
    }

}