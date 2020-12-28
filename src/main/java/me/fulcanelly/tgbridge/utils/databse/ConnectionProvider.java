package me.fulcanelly.tgbridge.utils.databse;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.SneakyThrows;

public class ConnectionProvider {
    
    JavaPlugin plugin;

    public ConnectionProvider(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @SneakyThrows
	public
    Connection getConnection() {
        var folder = this.plugin.getDataFolder();
        var path = new File(folder, "database.sqlite3").toString();
        return DriverManager.getConnection("jdbc:sqlite:" + path);
    }

}
