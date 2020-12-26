package me.fulcanelly.tgbridge.tapi.events;

import me.fulcanelly.tgbridge.utils.events.detector.Detector;
import me.fulcanelly.tgbridge.utils.events.pipe.EventObject;
import me.fulcanelly.tgbridge.tapi.Message;

import org.json.simple.JSONObject;

public class MessageEvent extends Message implements EventObject {
    //public Message message;
    
    <T>MessageEvent(T entries) {
        super(entries);
    }

    public static Detector<JSONObject> detector = update -> {
        Object message = update.get("message");

        if(message != null) {
            return new MessageEvent(message);
        }

        return null;
    };
}