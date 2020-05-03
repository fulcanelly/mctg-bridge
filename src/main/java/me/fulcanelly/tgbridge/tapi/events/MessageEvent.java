package me.fulcanelly.tgbridge.tapi.events;

import me.fulcanelly.tgbridge.utils.events.detector.EventDetectorManager;
import me.fulcanelly.tgbridge.utils.events.pipe.EventObject;
import me.fulcanelly.tgbridge.tapi.Message;

public class MessageEvent extends Message implements EventObject {
    //public Message message;
    
    <T>MessageEvent(T entries) {
        super(entries);
    }

    public static EventDetectorManager.Detector detector = update -> {
        //System.o
        Object message = update.get("message");

        if(message != null) {
            return new MessageEvent(message);
        }

        return null;
    };
}