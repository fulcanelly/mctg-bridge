package me.fulcanelly.tgbridge.utils.events.pipe;

import me.fulcanelly.tgbridge.utils.events.detector.Detector;

//public interface EventObject<D, R> {

public interface EventObject {
    Detector<?> detector = null;
}
