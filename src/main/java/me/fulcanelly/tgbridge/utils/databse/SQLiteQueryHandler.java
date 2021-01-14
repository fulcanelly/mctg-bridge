package me.fulcanelly.tgbridge.utils.databse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang.ClassUtils;

import lombok.SneakyThrows;
import me.fulcanelly.tgbridge.utils.async.AsyncActorEngine;
import me.fulcanelly.tgbridge.utils.async.tasks.AsyncTask;
import me.fulcanelly.tgbridge.utils.databse.tasks.AsyncSQLTask;
import me.fulcanelly.tgbridge.utils.stop.Stopable;
import net.md_5.bungee.api.ChatColor;


public class SQLiteQueryHandler implements Stopable {

    final Connection connection;
    final AsyncActorEngine engine;  

    public SQLiteQueryHandler(Connection conn) {
        connection = conn;
        this.engine = new AsyncActorEngine();
    }

    void logString(String to_log) {
        System.out.println("[" + Thread.currentThread() + "] "+ ChatColor.BLUE + to_log);
    }


    @SneakyThrows
    PreparedStatement setUpPStatementOf(String query, Object ...args) {
        var pstmt = connection
            .prepareStatement(query);
        
        this.setVars(pstmt, args);
        return pstmt;
    }

    @SneakyThrows
    static String getColumnLabelOf(ResultSetMetaData data, int index) {
        return data.getColumnLabel(index);
    }

    @SneakyThrows
    static Object getColumnByIndex(ResultSet set, int index) {
        return set.getObject(index);
    }

    @SneakyThrows
    public Map<String, Object> parseMapOfResultSet(ResultSet set) {

        ResultSetMetaData data = set.getMetaData();
        
        return IntStream.range(1, data.getColumnCount() + 1)
            .boxed()
            .collect(
                Collectors.toMap(
                    i -> getColumnLabelOf(data, i), 
                    i -> getColumnByIndex(set, i)
                )
        );
    }

    @SneakyThrows
    public List< Map<String, Object> > parseListOf(ResultSet set) {

        var list = new ArrayList< Map<String, Object> >();
        
        while (set.next()) {
            list.add(parseMapOfResultSet(set));
        }

        return list;
    }

    @SneakyThrows
    public int updateExecutor(String query, Object ...args) {
        logString(query);
        return setUpPStatementOf(query, args)
            .executeUpdate();
    }

    @SneakyThrows
    public ResultSet queryExecutor(String query, Object ...args) {
        logString(query);
        return setUpPStatementOf(query, args)
            .executeQuery();
    }

    public void execute(String query, Object... args) {
        new AsyncSQLTask<>(query, args, this::updateExecutor, engine)
            .addToQueue();
    }

    public AsyncTask<ResultSet> executeQuery(String query, Object... args) {
        return new AsyncSQLTask<>(query, args, this::queryExecutor, engine)
            .addToQueue();
    }


    @SneakyThrows
    void dispatchOneItem(PreparedStatement pstmt, int index, Object item) {
        Stream.of(PreparedStatement.class.getDeclaredMethods())
            .filter(meth -> 
                meth.getName().startsWith("set") && 
               !meth.getName().startsWith("setN")
            )
            .filter(one ->
                ClassUtils.isAssignable(one.getParameterTypes()[1], item.getClass(), true)
            )
            .findFirst().orElseThrow(() -> new RuntimeException("Unknown arg type"))
            .invoke(pstmt, index, item);
    }
    
    void setVars(PreparedStatement pstmt, Object ...list) {
        IntStream.range(0, list.length)
            .forEach(index -> dispatchOneItem(pstmt, index + 1, list[index]));
    }

    @Override
    public void stopIt() {
        engine.stopIt();
    }
 
}
