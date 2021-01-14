package me.fulcanelly.tgbridge.utils.stop;

import java.util.ArrayList;
import java.util.List;

public class StopHandler {
    List<Stopable> services = new ArrayList<>();

    public void register(Stopable item) {
        services.add(item);
    }

    public void stopAll() {
        services.forEach(one -> one.stopIt());
    }
}
