package me.fulcanelly.tgbridge.tools.stats;

import org.json.simple.JSONObject;
import java.util.HashMap;

public class StatsTable {
    public long total_time = 0l;
    public long last_point = -1l;
    public boolean is_online = false;

    static public StatsTable load(JSONObject obj) {
        StatsTable res = new StatsTable();

        res.total_time = (long) obj.get("total");
        res.last_point = (long) obj.get("last");

        return res;
    }

    public JSONObject jsonize() {
        HashMap<String, Long> result = new HashMap<>();

        result.put("total", total_time);
        result.put("last", last_point);

        return new JSONObject(result);
    }

    public synchronized void startTimer() {
        if (is_online) {
            return;
        }

        last_point = System.currentTimeMillis();
        is_online = true;
    }

    public synchronized StatsTable update() {
        if (is_online && last_point != -1) {
            long now = System.currentTimeMillis();
            total_time += now - last_point;
            last_point = now;
        }
        return this;
    }

    public void stop() {
        is_online = false;
    }

    // todo
    public String toString() {

        long seconds = total_time / 1000l;

        long hours = (seconds / 3600) % 60;
        long minutes = (seconds / 60) % 60;
        seconds = seconds % 60;

        StringBuilder builder = new StringBuilder();
        if (hours != 0) {
            builder.append(hours + " hrs ");
        }

        if (minutes != 0) {
            builder.append(minutes + " min ");
        }

        if (seconds != 0) {
            builder.append(seconds + " sec ");
        }

        return builder.toString();
    }
}