package me.fulcanelly.tgbridge.tapi.events;

import me.fulcanelly.tgbridge.utils.events.pipe.EventObject;

import java.util.Arrays;
import java.util.List;

import me.fulcanelly.tgbridge.tapi.Message;

public class CommandEvent extends Message implements EventObject {

    public String [] args;

    List<String> cache;
    
    public List<String> getArgs() {
        if (cache == null) {
            var argsReferenceCopy = args;
            if (argsReferenceCopy == null) {
                argsReferenceCopy = new String[0];
            }
            cache = Arrays.asList(argsReferenceCopy);
        }
        return cache;
    }
    
    public CommandEvent(Message msg) {
        super(msg);
    }

}