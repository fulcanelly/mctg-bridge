package me.fulcanelly.tgbridge.tools.stats;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.fulcanelly.tgbridge.TelegramBridge;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class StatCollector extends StatsDatabase implements Listener {
    
    public StatCollector(TelegramBridge plugin) {
        super(plugin);
        this.initTables();

        Bukkit.getOnlinePlayers()
            .forEach(player -> getStat(player.getName())
                .startTimer()
                .updateTable(this));
        plugin.commands.addCommand("top", this::getMessage);
    }

    private UserStats getStat(String name) {
        var stat = this.findByName(name);
        
        if (stat.isEmpty()) {
            return addUserStats(new UserStats(name));
        }

        return stat.get();
    }

    UserStats getStatsByEvent(PlayerEvent pevent) {
        String name = pevent.getPlayer().getName();
        return getStat(name);
    }

    UserStats getStatsByEvent(EntityEvent eevent) {
        String name = eevent.getEntity().getName();
        return getStat(name);
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        getStatsByEvent(event).startTimer().updateTable(this);
    }

    @EventHandler
    void onLeft(PlayerQuitEvent event) {
        getStatsByEvent(event).tick().updateTable(this);
    }

    @EventHandler
    void onDeath(PlayerDeathEvent event) {
        var it = getStatsByEvent(event);
        it.deaths++;
        it.updateTable(this);
    }
    
    //todo
    double getGreatestDeathPeriod() {
        var result = sqlhandler
            .executeQuery("SELECT * FROM user_stats ORDER BY  total_time / (deaths + 1.0) DESC")
            .waitForResult();

        return 
            parserFromResultSet(result)
            .getDeathPeriod();
    }

    private class MessageMaker {

        double max_death_period;
        String result = new String("Top 10 players: \n\n");

        MessageMaker() {
            this.max_death_period = getGreatestDeathPeriod();
        }

        void builder(UserStats stats) {

            double alive_coef = stats.getAliveCoefficient(max_death_period);
            
            result += String.format(
                " 🏳️‍🌈 `%s`\n" +
                "  played time — %s\n" + 
                "  deaths — %d\n" +
                "  survival rate — %.3f\n\n", 
                stats.name, stats.toString(), stats.deaths, alive_coef 
            );
        }

        String getString() {
            var list = getTopByTotalTime(10);
            list.forEach(this::builder);
            return result;
        }

    }

    public String getMessage() {

        if (this.getCount() == 0) { 
            return "No one played yet ...";
        }

        Bukkit.getOnlinePlayers().stream()
            .map(player -> getStat(player.getName()).tick())
            .forEach(table -> table.updateTable(this));

        return new MessageMaker().getString();
    }

}
