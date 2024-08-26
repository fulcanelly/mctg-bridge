package me.fulcanelly.tgbridge.tapi.events;


import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.fulcanelly.tgbridge.tapi.Message;

@RequiredArgsConstructor @Data
public class CommandEvent {

    public String [] args;

    List<String> cache;

   final Message message;
    
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


}