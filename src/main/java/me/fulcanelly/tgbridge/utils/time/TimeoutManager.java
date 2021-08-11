package me.fulcanelly.tgbridge.utils.time;

import java.util.concurrent.TimeUnit;

public class TimeoutManager {
    
    long last_update = System.currentTimeMillis();
    final long max_timeout;

    public TimeoutManager(long max_timeout_millis) {
        this.max_timeout = max_timeout_millis;
    }

    public void update() {
        last_update = System.currentTimeMillis();
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() - last_update > max_timeout;
    }
}
