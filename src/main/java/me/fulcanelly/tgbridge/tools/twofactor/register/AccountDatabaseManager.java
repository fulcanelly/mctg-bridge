package me.fulcanelly.tgbridge.tools.twofactor.register;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import lombok.SneakyThrows;
import me.fulcanelly.clsql.databse.SQLQueryHandler;


public class AccountDatabaseManager {
    @Inject
    SQLQueryHandler sql;

    long annihilationTime = TimeUnit.MINUTES.toMillis(15);

    @Inject
    public void setupTable() {
        sql.syncExecuteUpdate("CREATE TABLE IF NOT EXISTS mctg_accounts_mapping(" +
            "acc_map_entry_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER," +
            "player TEXT" +
            ")");
    }

    public void insertNew(long userId, String player) {
        sql.syncExecuteUpdate("INSERT INTO mctg_accounts_mapping(user_id, player) VALUES(?, ?)", userId, player);
    }

    //todo
    void delete() {

    }

    @SneakyThrows
    public Optional<Long> getTgByUsername(String username) {
        var result = sql.syncExecuteQuery("SELECT * FROM mctg_accounts_mapping WHERE player = ?", username);

        if (result.next()) {
            return Optional.of(
                Long.valueOf(sql.parseMapOfResultSet(result).get("user_id").toString())
            );
        }

        return Optional.empty();
    }

    @SneakyThrows
    public Optional<String> getUsernameByTg(long userId) {
        var result = sql.syncExecuteQuery("SELECT * FROM mctg_accounts_mapping WHERE user_id = ?", userId);

        if (result.next()) {
            return Optional.of((String)sql.parseMapOfResultSet(result).get("player"));
        }
        
        return Optional.empty();

    }
    
    @SneakyThrows
    public List<String> getUsernamesByTg(long userId) {
        var result = sql.syncExecuteQuery("SELECT * FROM mctg_accounts_mapping WHERE user_id = ?", userId);
        var list = new ArrayList<String>();

        while (result.next()) {
            list.add((String)sql.parseMapOfResultSet(result).get("player"));
        }
        
        return list;

    }

    
  
}
