package me.fulcanelly.tgbridge.utils.events.detector;

import me.fulcanelly.tgbridge.utils.events.pipe.EventObject;

public interface Detector<T> {
    EventObject is_it(T update);

}
