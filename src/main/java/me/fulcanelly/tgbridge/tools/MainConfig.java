package me.fulcanelly.tgbridge.tools;

import me.fulcanelly.tgbridge.utils.config.annotations.ConfigFile;
import me.fulcanelly.tgbridge.utils.config.annotations.Optional;
import me.fulcanelly.tgbridge.utils.config.annotations.Saveable;
import me.fulcanelly.tgbridge.utils.config.annotations.Nullable;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
/*
class LoginManager {
    Boolean enable;
};
class ConfigS {
    String api_token;
    String chat_id;
    LoginManager login_manger;
}
*/
@ConfigFile(file = "config.yml")
public class MainConfig {
    
    @Saveable
    public String api_token;

    @Saveable
    @Nullable
    public Integer chat_id;

    @Saveable
    public Boolean login_manger = false;


    @Saveable
    @Nullable
    @Optional
    public String test_field = "works";

   // public ConfigManager<MainConfig> manager;

    /*public MainConfig(JavaPlugin plugin) {
        File path = plugin.getDataFolder();
        manager = new ConfigManager<>(this, path);
        manager.setOnAbsent(manager::save);
    }*/
    
}