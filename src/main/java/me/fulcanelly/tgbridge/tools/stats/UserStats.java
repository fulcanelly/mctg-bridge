package me.fulcanelly.tgbridge.tools.stats;

import java.util.stream.Collectors;
import java.util.stream.Stream;

class TimeImage {
            
    Long time;
    String measure;

    TimeImage(long time, String measure) {
        this.time = time; 
        this.measure = measure;
    }

    Stream<String> toStream() {
        return Stream.of(time.toString(), measure);
    }
}

public class UserStats {
    public long total_time = 0l;
    
    public long last_point = -1l;
    
    public long deaths = 0;

    public String name = null;

    public UserStats(String name) {
        this.name = name;
    }

    public UserStats() {

    }
    
    double getDeathPeriod() {
        return total_time / (deaths + 1.0);
    }

    double getAliveCoefficient(double max) {
        return getDeathPeriod() / max;
    }


    public void updateTable(StatsDatabase stats) {
        stats.updateStats(this);
    }

    public UserStats tick() {
        if (last_point != -1) {
            long now = System.currentTimeMillis();
            total_time += now - last_point;
            last_point = now;
        }
       
        return this;
    }

    public UserStats startTimer() {
        last_point = System.currentTimeMillis();
        return this;
    }


    public String toString() {
      
        long seconds = total_time / 1000l;

        long hours = (seconds / 3600);
        long minutes = (seconds / 60) % 60;
        seconds = seconds % 60;    

        TimeImage entry[] = new TimeImage[]{
            new TimeImage(hours, "h "),
            new TimeImage(minutes, "m "),
            new TimeImage(seconds, "s "),
        };

        var joiner = Collectors.joining();
        
        return Stream.of(entry)
            .filter(row -> row.time != 0)
            .map(row -> row.toStream().collect(joiner))
            .collect(joiner);
    }
}
