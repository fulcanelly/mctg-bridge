package me.fulcanelly.tgbridge.utils.events.detector;

import me.fulcanelly.tgbridge.utils.events.pipe.EventObject;

public interface Detector<T, K> {
    EventObject is_it(T update, K data);

}
