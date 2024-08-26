package me.fulcanelly.tgbridge.tools.command.mc;

import me.fulcanelly.tgbridge.tools.command.mc.parser.CommandParser;
import me.fulcanelly.tgbridge.tools.command.mc.parser.CommandSchema;
import java.util.LinkedList;
import java.util.List;
import com.google.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

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
        var parser = new CommandParser(commandSchema, new LinkedList<String>(List.of(args)), sender);
        return parser.parse().getExpected();
    }

}
