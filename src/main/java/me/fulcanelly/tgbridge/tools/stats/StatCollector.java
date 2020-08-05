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
    public HashMap<String, StatsTable> stats = new HashMap<>();

    private StatCollector(TgBridge plugin) {
        this.plugin = plugin;
        this.load();
        Bukkit.getOnlinePlayers()
            .forEach(player -> getStat(player.getName()).startTimer());
        plugin.commands.addCommand("top", msg -> msg.reply(this.getMessage()) );

    }

    public static StatCollector instance = null;

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

        String result = new String("Top 10 players: \n\n");

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
                .limit(10)
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
