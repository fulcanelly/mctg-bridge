package tgbridge.tapi.events;

import tgbridge.tapi.Message;
import tgbridge.utils.events.pipe.EventObject;

public class CommandEvent extends Message implements EventObject {

    public <T>CommandEvent(T msg) {
        super(msg);
    }

}