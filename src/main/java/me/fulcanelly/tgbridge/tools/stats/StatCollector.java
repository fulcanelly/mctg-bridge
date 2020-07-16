package me.fulcanelly.tgbridge.tools.stats;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import lombok.SneakyThrows;
import me.fulcanelly.tgbridge.TgBridge;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;

import java.util.Map.Entry;


import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

class StatsTable {

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

public class StatCollector implements Listener {

    @SneakyThrows
    public synchronized void load() {
        File configFile = new File(plugin.getDataFolder(), "player-stats.json");
   
        JSONObject loaded = new JSONObject();

        if (!configFile.exists()) {
            return;
        }

        FileReader reader = new FileReader(configFile);
        Object data = new JSONParser().parse(reader);
        loaded = (JSONObject) data;
        
        loaded.forEach((name, stat) -> {
            stats.put((String)name, StatsTable.load((JSONObject)stat));
        });

    }
    
    public synchronized void save() {
        File configFile = new File(plugin.getDataFolder(), "player-stats.json");

        try {
            PrintWriter pw = new PrintWriter(configFile);
            pw.write(this.jsonize().toJSONString());
            pw.flush();
            pw.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    JSONObject jsonize() {
        HashMap<String, JSONObject> result = new HashMap<>();
        stats.forEach((name, stats) -> {
            result.put(name, stats.jsonize());
        });
        return new JSONObject(result);
    }

    TgBridge plugin;
    HashMap<String, StatsTable> stats = new HashMap<>();

    private StatCollector(TgBridge plugin) {
        this.plugin = plugin;
        this.load();
        Bukkit.getOnlinePlayers()
            .forEach(player -> getStat(player.getName()).startTimer());
        plugin.commands.addCommand("stats", msg -> msg.reply(this.getMessage()) );

    }

    static StatCollector instance = null;

    public static void initalize(TgBridge plugin) {
        
        if (instance != null ) {
            throw new RuntimeException("instance exists already");
        }

        instance = new StatCollector(plugin);
        plugin.getServer()
            .getPluginManager()
            .registerEvents(instance, plugin);
    }

    public static void stop() {
        if (instance != null) {
            instance.update();
            instance.save();
        }
    }

    StatsTable getStat(String name) {
        StatsTable stat = stats.get(name);
        if (stat == null) {
            stat = new StatsTable();
            stats.put(name, stat);
        }
        return stat;
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        getStat(name).startTimer();
    }

    @EventHandler
    void onLeft(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        getStat(name).update().stop();
    }

    synchronized void update() {
        stats.forEach((ignored_name, stat) -> stat.update());
    }
    
    private class MessageMaker {

        String result = new String("Played time: \n\n");

        <T extends Entry<String, StatsTable> > int comparator(T a, T b) {
            return (int)( b.getValue().total_time - a.getValue().total_time );
        }

        void builder(Entry<String, StatsTable> pair) {
            String player = pair.getKey();
            StatsTable stat = pair.getValue();

            result += String.format(
                " 🏳️‍🌈 `%s` %s\n", player, stat.toString()
            );
        }

        void buildUp() {
            stats.entrySet().stream()
                .sorted(this::comparator)
                .forEach(this::builder);
        }

        String get() {
            this.buildUp();
            return result;
        }
    }

    public String getMessage() {

        if (stats.size() == 0) { 
            return "no one played yet ...";
        }

        update();
        
        return new MessageMaker().get();
    }

}
