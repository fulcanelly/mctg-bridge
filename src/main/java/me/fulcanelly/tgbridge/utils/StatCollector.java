package me.fulcanelly.tgbridge.utils;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.fulcanelly.tgbridge.TgBridge;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class StatCollector implements Listener {

    TgBridge plugin;
    HashMap<String, Stats> stats = new HashMap<>();

    StatCollector(TgBridge plugin) {
        this.plugin = plugin;
        Bukkit.getOnlinePlayers()
            .forEach(player -> getStat(player.getName()).startTimer());
        plugin.commandManager.addCommand("stats", msg -> {
            msg.reply(this.getMessage());
        });

    }

    public static void initalize(TgBridge plugin) {
        StatCollector collector = new StatCollector(plugin);
        plugin.getServer()
            .getPluginManager()
            .registerEvents(collector, plugin);
    }

    class Stats {

        long total_time = 0l;
        long last_point = -1l;
        boolean and_is_online = false;

        public synchronized void startTimer() {
            if (and_is_online) {
                return;
            }

            last_point = System.currentTimeMillis();
            and_is_online = true;
        }

        public synchronized Stats update() {
            if (and_is_online && last_point != -1) {
                long now = System.currentTimeMillis();
                total_time += now - last_point;
                last_point = now;
            }
            return this;
        }

        public void stop() {
            and_is_online = false;
        }

        //todo 
        public String toString() {
            long seconds = total_time / 1000l;
   
            long hours = (seconds / 3600) % 60;
            long minutes = (seconds / 60) % 60;
            seconds = seconds % 60;

            StringBuilder builder = new StringBuilder();
            if (hours != 0) {
                builder.append(hours + " hours ");
            }
            
            if (minutes != 0) {
                builder.append(minutes + " minutes ");
            }        
            
            if (seconds != 0) {
                builder.append(seconds + " seconds ");
            }
            
            return builder.toString();
        }
    }

    Stats getStat(String name) {
        System.out.println("getting \"" + name + "\"");
        Stats stat = stats.get(name);
        if (stat == null) {
            stat = new Stats();
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

        String result = new String();

        String get() {
            stats.forEach((player, stat) -> {
                result += String.format(
                    "%s played %s\n", UsefulStuff.formatMarkdown(player), stat.toString()
                );
            });
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
