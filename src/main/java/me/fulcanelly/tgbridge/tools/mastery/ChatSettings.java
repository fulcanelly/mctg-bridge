package me.fulcanelly.tgbridge.tools.mastery;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.fulcanelly.clsql.async.tasks.AsyncTask;
import me.fulcanelly.clsql.databse.SQLQueryHandler;

/**
 * termplate how it should look like:
 * 
 * tg chat show
 * tg chat hide
 */

import me.fulcanelly.tgbridge.view.NamedTabExecutor;

public class ChatSettings {

    SQLQueryHandler sql;
    Boolean defaultHide = false;

    @Inject
    public ChatSettings(SQLQueryHandler sql) {
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
                    sql.syncExecuteUpdate("UPDATE chat_visibility_settings SET player = ?, hide = ? WHERE player = ?", prepearedPlayer, status, prepearedPlayer);
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
                    return (Integer)fetched.get().get("hide") != 0;
                }
            });
    }


    Deque<String> prepArgs(String[] args) {
        return Stream.of(args)
            .filter(one -> one.length() != 0)
            .collect(Collectors.toCollection(ArrayDeque::new));
    }

    public void makeChatShow(CommandSender sender) {
        setPlayerVisibilityTo(sender.getName(), false);
        sender.sendMessage("chat will be shown");
    }

    public void makeChatHide(CommandSender sender) {
        setPlayerVisibilityTo(sender.getName(), true);
        sender.sendMessage("chat will be hidden");
    }
    

    void handleChat(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("no enough arguments");
        } else {
            switch (args[1]) {
                case "show":
                    setPlayerVisibilityTo(sender.getName(), false);
                    sender.sendMessage("chat will be shown");
                break;

                case "hide":
                    setPlayerVisibilityTo(sender.getName(), true);
                    sender.sendMessage("chat will be hiden");
                break;

                default:
                    sender.sendMessage("usage: /tg char <show | hide>");

            }
        }
    }
    /*
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args[0]) {
            case "chat":
                handleChat(sender, args);
            break;
            default:
                sender.sendMessage("unknown subcommand");
            break;
        }
        return true;
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
        }* /

         
       // return null;
    }
    */

    List<String> checkList(String arg, Deque<String> hz) {
        if (arg.equals("chat")) {
            hz.clear();
            return List.of("show", "hide");
        } else {
            return null;
        }
    }


   

}



