package me.fulcanelly.tgbridge.tools.command.tg;

import com.google.inject.Inject;

import org.bukkit.Bukkit;

import lombok.AllArgsConstructor;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class StatsCommand implements CommandRegister {

    StatCollector statCollector;
    @Override
    public void registerCommand(CommandManager manager) {
        new ReplierBuilder("stats", this::onEvent).registerCommand(manager);        
    }

    String onEvent(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            return "specify nickname to get stats";
        } 
          
        var nick = event.args[0];
        var optStats = statCollector
            .findByName(nick)
            .waitForResult();
                            
        if (optStats.isEmpty()) {
            return "no players whith such nickname yet";
        } else {
            var stats = optStats.get();
            
            String online_sign = Bukkit.getPlayer(stats.name) != null ? "❇️" : "";

            String data = String.format(
                " 🏳️‍🌈 `%s` " + online_sign + '\n' +
                "  played time — %s\n" + 
                "  deaths — %d\n",
                stats.name, stats.toString(), stats.deaths
            );

            return data;
        }
    }
}
