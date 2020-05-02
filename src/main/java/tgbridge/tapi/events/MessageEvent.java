package tgbridge.tapi.events;

import tgbridge.tapi.Message;
import tgbridge.utils.events.detector.EventDetectorManager.Detector;
import tgbridge.utils.events.pipe.EventObject;

public class MessageEvent extends Message implements EventObject {
    //public Message message;
    
    <T>MessageEvent(T entries) {
        super(entries);
    }

    public static Detector detector = update -> {
        //System.o
        Object message = update.get("message");

        if(message != null) {
            return new MessageEvent(message);
        }

        return null;
    };
}