package me.fulcanelly.tgbridge.utils;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayDeque;
import java.util.Queue;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DeepLoger implements Listener {


    Queue<BlockEvent> queue = new ArrayDeque<>();

    //todo
    void logDeamon() {
        new Thread(() -> {

        }).start();
    }
    
    public static void initalize(JavaPlugin plugin) {
        Listener deep_loger = new DeepLoger();
        plugin.getServer()
            .getPluginManager()
            .registerEvents(deep_loger, plugin);
    }

    @EventHandler
    void onBreak(BlockBreakEvent event ) {
     //   queue.add(event);
        System.out.println(
            event.getPlayer().getName() + " break block at " + event.getBlock().getLocation().toString()
        );
    }

    @EventHandler
    void onPlace(BlockPlaceEvent event) {
       // queue.add(event);

        System.out.println(
            event.getPlayer().getName() + " placed block at " + event.getBlock().getLocation().toString()
        );
    }

}