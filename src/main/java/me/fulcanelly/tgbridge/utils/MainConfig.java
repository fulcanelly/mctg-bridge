package me.fulcanelly.tgbridge.utils;

import me.fulcanelly.tgbridge.tools.config.*;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

@ConfigFile(file = "config.json")
public class MainConfig {
    
    @Saveable
    public String api_token = "token of telegram bot";

    @Saveable
    public String chat_id = "id of telegram chat where bot suppose to work";

    @Saveable
    public Boolean login_manger = false;

    @Saveable
    public String test_field = "works";

    public ConfigManager<MainConfig> manager;

    public MainConfig(JavaPlugin plugin) {
        File path = plugin.getDataFolder();
        manager = new ConfigManager<>(this, path);
        manager.setOnAbsent(manager::save);
    }
    
}