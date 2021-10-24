package me.fulcanelly.tgbridge.tools.command.mc;

import static me.fulcanelly.tgbridge.tools.command.mc.parser.CommandBuilder.*;

import me.fulcanelly.tgbridge.tools.command.mc.parser.ArgumentBuilder;
import me.fulcanelly.tgbridge.tools.command.mc.parser.CommandParser;
import me.fulcanelly.tgbridge.tools.command.mc.parser.CommandSchema;
import me.fulcanelly.tgbridge.tools.mastery.ChatSettings;

import me.fulcanelly.tgbridge.tools.twofactor.InGameReceptionUI;
import me.fulcanelly.tgbridge.utils.events.pipe.Listener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
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
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import me.fulcanelly.clsql.async.tasks.AsyncTask;
import me.fulcanelly.clsql.databse.SQLQueryHandler;

public class CommandProcessor implements TabExecutor {


    @Inject  
    CommandSchema commandSchema;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        var parser = new CommandParser(commandSchema, new LinkedList<String>(List.of(args)), sender);
        parser.evaluate();
        return true;
    }

 
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    
    }

}
