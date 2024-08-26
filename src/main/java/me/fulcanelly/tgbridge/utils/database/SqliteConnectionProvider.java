package me.fulcanelly.tgbridge.utils.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import com.google.inject.Inject;
import com.google.inject.name.Named;


import lombok.SneakyThrows;

public class SqliteConnectionProvider {

    final File folder;
    
    @Inject
    public SqliteConnectionProvider(@Named("plugin_folder") File folder) {
        this.folder = folder;
    }

    @SneakyThrows
	public Connection getConnection() {
        var path = new File(folder, "database.sqlite3").toString();
        return DriverManager.getConnection("jdbc:sqlite:" + path);
    }
}
