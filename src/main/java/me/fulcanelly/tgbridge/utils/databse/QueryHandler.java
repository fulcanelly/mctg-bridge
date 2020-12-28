package me.fulcanelly.tgbridge.utils.databse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang.ClassUtils;

import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;


public class QueryHandler {
    final Connection connection;

    public QueryHandler(Connection conn) {
        connection = conn;
    }

    void logString(String to_log) {
        System.out.println("[" + Thread.currentThread() + "] "+ ChatColor.BLUE + to_log);
    }

    @SneakyThrows
    public int execute(String query, Object ...args) {
        logString(query);
        return setUpPStatementOf(query, args)
            .executeUpdate();
    }

    @SneakyThrows
    public ResultSet executeQuery(String query, Object ...args) {
        logString(query);
        return setUpPStatementOf(query, args)
            .executeQuery();
    }

    @SneakyThrows
    PreparedStatement setUpPStatementOf(String query, Object ...args) {
        var pstmt = connection
            .prepareStatement(query);
        
        this.setVars(pstmt, args);
        return pstmt;
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
 
}
