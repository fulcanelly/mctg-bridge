package me.fulcanelly.tgbridge.utils.events.detector;

import me.fulcanelly.tgbridge.utils.events.pipe.EventObject;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventDetectorManager {

    EventPipe pipe = null;
    EventObject event = null;
    List<Detector> detectors = new ArrayList<>();

    public interface Detector {
        EventObject is_it(JSONObject update);
    }

    public EventDetectorManager(EventPipe pipe) {
        this.pipe = pipe;
    }

    public EventDetectorManager setPipe(EventPipe p) {
        this.pipe = p;
        return this;
    }

    public EventDetectorManager addDetector(Detector d) {
        detectors.add(d);
        return this;
    }
    
    private void checkDetector(Detector detector) {
        event = detector.is_it(update);
        if (event != null) {
            pipe.emit(event);
        }
    }

    JSONObject update = null;

    synchronized public void handle(JSONObject update) {
        this.update = update;
        detectors.stream()
                .forEach(this::checkDetector);
    }
}
