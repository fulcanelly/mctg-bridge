package me.fulcanelly.tgbridge.tools.twofactor.register;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import lombok.SneakyThrows;
import me.fulcanelly.clsql.databse.SQLQueryHandler;

public class RegisterDatabaseManager {
    
    @Inject
    SQLQueryHandler sql;

    long annihilationTime = TimeUnit.MINUTES.toMillis(15);

    public void setupTable() {
        sql.syncExecuteUpdate("CREATE TABLE IF NOT EXISTS registration(" +
            "reg_entry_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "code TEXT," +
            "player TEXT," +
            "creation_time INTEGER" +
            ")");
    }

    public void insertNew(String player, String code) {
        forgetOld();
        sql.syncExecuteUpdate("INSERT INTO registration(code, player, creation_time) VALUES(?, ?, ?)", code, player, System.currentTimeMillis());
    }

    public void delete(String player, String code) {
        sql.syncExecuteUpdate("DELETE FROM registration WHERE player = ? AND code = ?", player, code);
    }

    public void forgetOld() {
        sql.syncExecuteUpdate("DELETE FROM registration WHERE (creation_time + ?) <= ?", annihilationTime, System.currentTimeMillis());
    }

    @SneakyThrows
    public boolean isValidCode(String player, String code) {
        forgetOld();
        return sql.syncExecuteQuery("SELECT * FROM registration WHERE code = ? AND player = ?", code, player).next();
    }
}
