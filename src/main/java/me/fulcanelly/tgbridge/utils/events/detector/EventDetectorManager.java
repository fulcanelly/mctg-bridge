package me.fulcanelly.tgbridge.utils.events.detector;

import java.util.List;
import java.util.ArrayList;

import me.fulcanelly.tgbridge.utils.events.pipe.EventObject;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;
import org.json.simple.JSONObject;

import tgbridge.utils.events.pipe.*;

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

    public void handle(JSONObject update) {
        detectors.stream()
        .forEach(detector -> {
            event = detector.is_it(update);
            if(event != null) {
                pipe.emit(event);
            }
        });
    }
}