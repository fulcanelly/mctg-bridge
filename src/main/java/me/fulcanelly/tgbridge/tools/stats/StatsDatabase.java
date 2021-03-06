package me.fulcanelly.tgbridge.tools.stats;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.SneakyThrows;

import me.fulcanelly.clsql.databse.SQLQueryHandler;
import me.fulcanelly.clsql.reflect.ObjectMapper;
import me.fulcanelly.clsql.async.tasks.AsyncTask;

public class StatsDatabase {

    final SQLQueryHandler qhandler;
    final ObjectMapper mapper;

    StatsDatabase(SQLQueryHandler sqlite) {//
        this.mapper = new ObjectMapper();
        this.qhandler = sqlite;
    }   

    @SneakyThrows
    List<UserStats> getTopByTotalTime(int amount) {

        ResultSet rset = qhandler
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

        ResultSet result = qhandler
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
        qhandler.execute("CREATE TABLE IF NOT EXISTS user_stats(\n" +
            " total_time LONG NOT NULL,\n" +
            " last_point LONG,\n" + 
            " deaths LONG,\n" +
            " name STRING PRIMARY KEY NOT NULL\n" +
        ");");
    }

    @SneakyThrows
    UserStats addUserStats(UserStats stats) {
        qhandler.execute("INSERT INTO user_stats VALUES(?, ?, ?, ?)", 
            stats.total_time, stats.last_point, stats.deaths, stats.name);

        return stats;
    }

    @SneakyThrows
    void updateStats(UserStats stats) {
        qhandler.execute("UPDATE user_stats SET total_time = ?, last_point = ?, deaths = ? WHERE name = ?",
            stats.total_time, stats.last_point, stats.deaths, stats.name);
    }

    @SneakyThrows
    UserStats parserFromResultSet(ResultSet rset) {
        Map<String, Object> map = qhandler.parseMapOfResultSet(rset);
        map.put("name", String.valueOf(map.get("name")));
        return mapper.convertValue(map, UserStats.class);
    }

    static Optional<UserStats> empty = Optional.empty();

    @SneakyThrows
    Optional<UserStats> getOptionalUserStatsOf(ResultSet set) {
        if (set.next()) {
            try {
                return Optional.of(parserFromResultSet(set));
            } catch(Throwable e) {
                return empty;
            }
        } else {
            return empty;
        }

    }

    @SneakyThrows
    public AsyncTask<Optional<UserStats>> findByName(String name) {
        return qhandler
            .executeQuery("SELECT * FROM user_stats WHERE name = ?", name)
            .andThen(this::getOptionalUserStatsOf);
    }

    
}
