package me.fulcanelly.tgbridge.utils;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayDeque;
import java.util.Queue;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.block.data.type.*;

public class DeepLoger implements Listener {


    Queue<BlockBreakEvent> queue = new ArrayDeque<>();

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

       // if (event.getBlock().getBlockData() instanceof Leaves) {
       //     Leaves leaf = (Leaves) event.getBlock().getBlockData();
      //      System.out.println(leaf.isPersistent​());
       // }

        System.out.println(
            event.getPlayer().getName() + " break block at " + event.getBlock().getLocation().toString()
        );
    }

    @EventHandler
    @Deprecated
    void onPlace(BlockPlaceEvent event) {
        
        System.out.println(
            event.getPlayer().getName() + " placed block at " + event.getBlock().getLocation().toString()
        );
    }

}