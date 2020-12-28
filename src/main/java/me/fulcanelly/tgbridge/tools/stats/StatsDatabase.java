package me.fulcanelly.tgbridge.tools.stats;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.SneakyThrows;
import me.fulcanelly.tgbridge.TelegramBridge;

import me.fulcanelly.tgbridge.utils.databse.LazySQLActor;

public class StatsDatabase {

    StatsDatabase(TelegramBridge plugin) {
        this.sqlhandler = plugin.getSQLhandler();
    }

    LazySQLActor sqlhandler;

    @SneakyThrows
    List<UserStats> getTopByTotalTime(int amount) {

        ResultSet rset = sqlhandler
            .executeQuery("SELECT * FROM user_stats ORDER BY total_time DESC LIMIT ?", amount)
            .waitForResult();

        List<UserStats> list = new ArrayList<>();

        while (rset.next()) {
            list.add(parserFromResultSet(rset));
        }

        return list;
    }

    @SneakyThrows
    long getCount() {

        ResultSet result = this.sqlhandler
            .executeQuery("SELECT count(*) FROM user_stats")
            .waitForResult();

        if (result.next()) {
            return result.getLong(1);
        } else {
            throw new RuntimeException("Idk");
        }
    }


    @SneakyThrows
    void initTables() {
      

        sqlhandler.execute("CREATE TABLE IF NOT EXISTS user_stats(\n" +
            "total_time LONG NOT NULL,\n" +
            "last_point LONG,\n" + 
            "deaths LONG,\n" +
            "name STRING PRIMARY KEY NOT NULL\n" +
        ");");
    }

    @SneakyThrows
     UserStats addUserStats(UserStats stats) {
        sqlhandler.execute(
            "INSERT INTO user_stats VALUES(?, ?, ?, ?)", 
            stats.total_time, stats.last_point, stats.deaths, stats.name);

        return stats;
    }

    @SneakyThrows
     void updateStats(UserStats stats) {
        sqlhandler.execute(
            "UPDATE user_stats SET total_time = ?, last_point = ?, deaths = ? WHERE name = ?",
            stats.total_time, stats.last_point, stats.deaths, stats.name);
    }

    @SneakyThrows
     UserStats parserFromResultSet(ResultSet rset) {
        return new UserStats() {
            @SneakyThrows
            UserStats setFields() {
                total_time = rset.getLong("total_time");
                last_point = rset.getLong("last_point");
                deaths = rset.getLong("deaths");
                name = rset.getString("name");
                return this;
            }
        }.setFields();
    }

    @SneakyThrows
    public Optional<UserStats> findByName(String name) {   
        
        ResultSet result = this.sqlhandler.executeQuery(
            "SELECT * FROM user_stats WHERE name = ?", name)
            .waitForResult();

        if (!result.next()) {
            return Optional.empty();
        }

        return Optional.of(parserFromResultSet(result));
    }
}
