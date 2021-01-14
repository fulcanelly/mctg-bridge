package me.fulcanelly.tgbridge.tapi.events;

import me.fulcanelly.tgbridge.utils.events.detector.Detector;
import me.fulcanelly.tgbridge.utils.events.pipe.EventObject;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.TGBot;

import org.json.simple.JSONObject;

public class MessageEvent extends Message implements EventObject {
    //public Message message;
    
    <T>MessageEvent(T entries, TGBot bot) {
        super(entries, bot);
    }

    public static Detector<JSONObject, TGBot> detector = (update, bot) -> {
        Object message = update.get("message");

        if(message != null) {
            return new MessageEvent(message, bot);
        }

        return null;
    };
}