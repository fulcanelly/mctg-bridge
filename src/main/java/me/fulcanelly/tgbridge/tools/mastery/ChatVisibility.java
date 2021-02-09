package me.fulcanelly.tgbridge.tools.mastery;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import me.fulcanelly.tgbridge.view.NamedTabExecutor;

public class ChatVisibility implements NamedTabExecutor {

    SQLQueryHandler sql;
    Boolean defaultHide = false;

    public ChatVisibility(SQLQueryHandler sql) {
        this.sql = sql;
        sql.syncExecuteUpdate(
            "CREATE TABLE IF NOT EXISTS chat_visibility_settings(" +
            "    player STRING," +
            "    hide BOOL" +
            ")"
        );
    }

    public void setPlayerVisibilityTo(String player, boolean status) {
        var prepearedPlayer = player + "_";

        sql.executeQuery("SELECT * FROM chat_visibility_settings WHERE player = ?", prepearedPlayer)
            .andThen(sql::safeParseOne)
            .andThenSilently(fetched -> {
                if (fetched.isEmpty()) {
                    sql.syncExecuteUpdate("INSERT INTO chat_visibility_settings VALUES(?, ?)", prepearedPlayer, status);
                } else {
                    sql.syncExecuteUpdate("UPDATE TABLE chat_visibility_settings SET player = ?, hide = ?", player, status);
                }
            });
    }

    public AsyncTask<Boolean> getPlayerVisibility(String player) {
        var prepearedPlayer = player + "_";

        return sql.executeQuery("SELECT * FROM chat_visibility_settings WHERE player = ?", prepearedPlayer)
            .andThen(sql::safeParseOne)
            .andThen(fetched -> {
                if (fetched.isEmpty()) {
                    return defaultHide;
                } else {
                    return (Boolean)fetched.get().get("hide");
                }
            });
    }

    @Override 
    public String getCommandName() {
        return "tg";
    }

    Deque<String> prepArgs(String[] args) {
        return Stream.of(args)
            .filter(one -> one.length() != 0)
            .collect(Collectors.toCollection(ArrayDeque::new));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args[0]) {
          //  case "tg" 
        }
        sender.sendMessage("onCommand() = " + label + " " + sender.getName() + " " + List.of(args));
        return false;
    }

    List<String> checkList(String arg, Deque<String> hz) {
        if (arg.equals("chat")) {
            return List.of("show", "ignore");
        } else {
            return null;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        var normArgs = prepArgs(args);

        if (normArgs.size() == 0) {
            return List.of("chat", "help");
        }
    
        return checkList(normArgs.pollFirst(), normArgs);
        /*
        
        if (args[1] == "chat") {
            return List.of("show", "ignore");
        } else if (args[1] == "login") {
            return List.of("change", "ignore");
        } else if (args[1] == "help") {
        }*/

         
       // return null;
    }

}
