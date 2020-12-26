package me.fulcanelly.tgbridge.utils.events.detector;

import me.fulcanelly.tgbridge.utils.events.pipe.EventObject;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;

import java.util.ArrayList;
import java.util.List;

class EventDetectorManagerData<U, D extends Detector<U>> {

    EventPipe pipe = null;
    EventObject event = null;
    List<D> detectors = new ArrayList<>();
    
} 

public class EventDetectorManager<U> extends EventDetectorManagerData<U, Detector<U>> {

    public EventDetectorManager(EventPipe pipe) {
        this.pipe = pipe;
    }

    public EventDetectorManager<U> setPipe(EventPipe p) {
        this.pipe = p;
        return this;
    }

    public EventDetectorManager<U> addDetector(Detector<U> detector) {
        detectors.add(detector);
        return this;
    }

    public void handle(U update) {
        this.detectors
            .parallelStream()
            .map(detector -> detector.is_it(update))
            .filter(result -> result != null)
            .forEach(event -> pipe.emit(event));
    }
}
