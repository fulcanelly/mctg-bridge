package me.fulcanelly.tgbridge.tools.command.mc;

import me.fulcanelly.tgbridge.utils.events.pipe.Listener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.fulcanelly.clsql.async.tasks.AsyncTask;
import me.fulcanelly.clsql.databse.SQLQueryHandler;

public class CommandProcessor implements TabCompleter, CommandExecutor {


    void setup() {
      
    }




    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       

        return true;
    }

 


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
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
