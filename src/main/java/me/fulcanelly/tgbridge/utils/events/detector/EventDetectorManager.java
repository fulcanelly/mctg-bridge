package me.fulcanelly.tgbridge.utils.events.detector;

import me.fulcanelly.tgbridge.utils.events.pipe.EventObject;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;

import java.util.ArrayList;
import java.util.List;

class EventDetectorManagerData<U, K, D extends Detector<U, K>> {

    EventPipe pipe = null;
    EventObject event = null;
    List<D> detectors = new ArrayList<>();
    
} 

public class EventDetectorManager<U, K> extends EventDetectorManagerData<U, K, Detector<U, K>> {

    public EventDetectorManager(EventPipe pipe) {
        this.pipe = pipe;
    }

    public EventDetectorManager<U, K> setPipe(EventPipe p) {
        this.pipe = p;
        return this;
    }

    public EventDetectorManager<U, K> addDetector(Detector<U, K> detector) {
        detectors.add(detector);
        return this;
    }

    public void handle(U update, K data) {
        this.detectors.stream()
            .map(detector -> detector.is_it(update, data))
            .filter(result -> result != null)
            .forEach(event -> pipe.emit(event));
    }
}
