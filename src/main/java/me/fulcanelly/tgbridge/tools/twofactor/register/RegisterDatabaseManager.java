package me.fulcanelly.tgbridge.tools.twofactor.register;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import lombok.SneakyThrows;
import me.fulcanelly.clsql.databse.SQLQueryHandler;

public class RegisterDatabaseManager {

    @Inject
    SQLQueryHandler sql;

    long annihilationTime = TimeUnit.HOURS.toMillis(2);

    @Inject
    public void setupTable() {
        sql.syncExecuteUpdate("CREATE TABLE IF NOT EXISTS registration(" +
            "reg_entry_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "code TEXT," +
            "player TEXT," +
            "creation_time INTEGER" +
            ")");
    }

    public boolean insertNewIfCodeIsFree(String player, String code) {
        forgetOld();
        if (isCodeRegistered(code)) {
            return false;
        }

        sql.syncExecuteUpdate("INSERT INTO registration(code, player, creation_time) VALUES(?, ?, ?)", code, player, System.currentTimeMillis());
        return true;
    }

    public void delete(String code) {
        sql.syncExecuteUpdate("DELETE FROM registration WHERE code = ?", code);
    }

    public void forgetOld() {
        sql.syncExecuteUpdate("DELETE FROM registration WHERE (creation_time + ?) <= ?", annihilationTime, System.currentTimeMillis());
    }

    @SneakyThrows
    public boolean isCodeRegistered(String code) {
        forgetOld();
        return sql.syncExecuteQuery("SELECT * FROM registration WHERE code = ?", code).next();
    }

    @SneakyThrows
    public Optional<String> getPlayerByCode(String code) {
        forgetOld();
        var result = sql.syncExecuteQuery("SELECT * FROM registration WHERE code = ?", code);
        if (result.next()) {
            return Optional.of((String) sql.parseMapOfResultSet(result).get("player"));
        }

        return Optional.empty();
    }
}
