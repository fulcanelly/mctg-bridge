package me.fulcanelly.tgbridge.tools.stats;

import org.json.simple.JSONObject;

import me.fulcanelly.tgbridge.utils.config.Saveable;

import java.util.HashMap;

public class StatsTable {
    
    @Saveable
    public long total_time = 0l;
    
    @Saveable
    public long last_point = -1l;
    
    @Saveable
    public long deaths = 0;

    public boolean is_online = false;

    static public StatsTable load(JSONObject obj) {
        StatsTable res = new StatsTable();

        res.total_time = (long) obj.get("total");
        res.last_point = (long) obj.get("last");
        Long deaths = (Long) obj.get("deaths");
        if (deaths == null) {
            deaths = 0l;
        }
        res.deaths = deaths;
        return res;
    }

    public JSONObject jsonize() {
        HashMap<String, Long> result = new HashMap<>();

        result.put("total", total_time);
        result.put("last", last_point);
        result.put("deaths", deaths);

        return new JSONObject(result);
    }

    double getDeathPeriod() {
        return total_time / (deaths + 1.0);
    }

    double getAliveCoefficient(double max) {
        return getDeathPeriod() / max;
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

    public void stopTimer() {
        is_online = false;
    }

    // todo
    public String toString() {

        long seconds = total_time / 1000l;

        long hours = (seconds / 3600);
        long minutes = (seconds / 60) % 60;
        seconds = seconds % 60;

        StringBuilder builder = new StringBuilder();
        if (hours != 0) {
            builder.append(hours + "h ");
        }

        if (minutes != 0) {
            builder.append(minutes + "m ");
        }

        if (seconds != 0) {
            builder.append(seconds + "s ");
        }

        return builder.toString();
    }
}