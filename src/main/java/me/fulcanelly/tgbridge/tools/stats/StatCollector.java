package me.fulcanelly.tgbridge.tools.stats;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.fulcanelly.clsql.async.tasks.AsyncTask;
import me.fulcanelly.clsql.databse.SQLQueryHandler;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;

import com.google.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class StatCollector extends StatsDatabase implements Listener {
    
    SignupLoginReception reception;

    @Inject
    public StatCollector(SQLQueryHandler sqlite,  SignupLoginReception reception) {
        super(sqlite);
        this.reception = reception;
        this.initTables();

        Bukkit.getOnlinePlayers()
            .forEach(player -> getStat(player.getName())
                .andThenSilently(stats -> stats.startTimer()
                    .updateTable(this))
            );

    }

    private AsyncTask<UserStats> getStat(String name) {
        return this
            .findByName(name)
            .andThen(stats -> {
                if (stats.isEmpty()) {
                    return addUserStats(new UserStats(name));
                }
        
                return stats.get();
            });
    }

    AsyncTask<UserStats> getStatsByEvent(PlayerEvent pevent) {
        String name = pevent.getPlayer().getName();
        return getStat(name);
    }

    AsyncTask<UserStats> getStatsByEvent(EntityEvent eevent) {
        String name = eevent.getEntity().getName();
        return getStat(name);
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        getStatsByEvent(event)
            .andThenSilently(stats -> stats
                .startTimer()
                .updateTable(this));
    }

    @EventHandler
    void onLeft(PlayerQuitEvent event) {
        getStatsByEvent(event).andThenSilently(stats -> stats
            .tick()
            .updateTable(this));
    }

    @EventHandler
    void onDeath(PlayerDeathEvent event) {
        getStatsByEvent(event).andThenSilently(stats -> {
            stats.deaths++;
            stats.updateTable(this);
        });
    }
    
    double getGreatestDeathPeriod() {
        var result = qhandler
            .executeQuery("SELECT * FROM user_stats ORDER BY total_time / (deaths + 1.0) DESC")
            .waitForResult();

        return this
            .parserFromResultSet(result)
            .getDeathPeriod();
    }

    private class MessageMaker {

        double max_death_period;
        String result = new String("Top 5 players: \n\n");

        MessageMaker() {
            this.max_death_period = getGreatestDeathPeriod();
        }

        boolean isPlayerOnline(String name) {
            return Bukkit.getPlayer(name) != null;
        }

        void builder(UserStats stats) {

            double alive_coef = stats.getAliveCoefficient(max_death_period);
            
            String online_sign = isPlayerOnline(stats.name) ? "❇️" : "";
            String tg_bound_sign = reception.getTgByUser(stats.name)
                .map(__ -> "💦")
                .orElse("");

            result += String.format(
                " 🏳️‍🌈 `%s` " + online_sign + tg_bound_sign +'\n' +
                "  played time — %s\n" + 
                "  deaths — %d\n" +
                "  survival rate — %.3f\n\n", 
                stats.name, stats.toString(), stats.deaths, alive_coef 
            );
        }

        String getString() {
            getTopByTotalTime(5).forEach(this::builder);
            return result;
        }

    }

    public String getMessage() {

        if (this.getCount() == 0) { 
            return "No one played yet ...";
        }

        Bukkit.getOnlinePlayers().stream()
            .map(player -> getStat(player.getName()))
            .forEach(promise -> promise
                .andThenSilently(table -> {
                    table.tick();
                    table.updateTable(this);
            }));

        return new MessageMaker().getString();
    }

}
