package me.fulcanelly.tgbridge.tools.stats;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import lombok.SneakyThrows;
import me.fulcanelly.tgbridge.TelegramBridge;
import me.fulcanelly.tgbridge.utils.UsefulStuff;
import me.fulcanelly.tgbridge.utils.config.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;

import java.util.stream.Collectors;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

/*
class DatabaseWorker {
    Connection connection;
    String path;

    public DatabaseWorker(File file) {
        path = file.toString();
        getSQLConnection();
    }

    String getSignatureFromClass(Class<?> klazz) {
        return Arrays.stream(klazz.getDeclaredFields())
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .filter(field -> field.isAnnotationPresent(Saveable.class))
            .map(field -> "BLOB " + field.getName())
            .collect(Collectors.joining(", "));
    }

    @SneakyThrows
    void execSQL(String sql) {
        Statement statement = getSQLConnection().createStatement();
        statement.execute(sql);
        statement.close();
    }

    public void defineTable(String name, Class<?> klazz) {
        String signature = getSignatureFromClass(klazz);
        createTable(name, signature);
    }
    

    void createTable(String name, String signature) {
        String request = String.format(
            createTableQuery, name, signature);
        execSQL(request);
    }

    public void defineTable(String name, String ...columns) {
        String signature = Arrays.stream(columns)
            .map(col -> "BLOB " + col )
            .collect(Collectors.joining(",\n"));
        createTable(name, signature);
    }

    final static String createTableQuery = "CREATE TABLE IF NOT EXISTS %s(%s)";

    @SneakyThrows
    public Connection getSQLConnection() {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        }
        return connection;
    }
}

class StatsDataManager {

    DatabaseWorker dbase;
    StatsDataManager(TgBridge plugin) {
        File dbPathFile = new File(plugin.getDataFolder(), "db.sqlite");
        dbase = new DatabaseWorker(dbPathFile);
        dbase.defineTable("played_time", 
            "name", "total_time", "last_point");
    }

    final static String getOneByNick = "SELECT * FROM played_time WHERE name = ?";

    @SneakyThrows
    StatsTable get(String name) {
        PreparedStatement pstatement = dbase.getSQLConnection().prepareStatement(getOneByNick);
        pstatement.setString(1, name);
        ResultSet rset = pstatement.executeQuery();
        if (rset.next()) {
            long total = rset.getLong("total_time");
            long last = rset.getLong("last_point");
            return StatsTable.load(total, last);
        }

        return null;
    }

    @SneakyThrows
    void put(StatsTable stat) {
        PreparedStatement pstatement = dbase.getSQLConnection().prepareStatement("sql");
    }
}
*/
public class StatCollector implements Listener {

    @SneakyThrows
    public synchronized void load() {
        File configFile = new File(plugin.getDataFolder(), "player-stats.json");
   
        JSONObject loaded = new JSONObject();

        if (!configFile.exists()) {
            return;
        }

        FileReader reader = new FileReader(configFile);
        Object data = new JSONParser().parse(reader);
        loaded = (JSONObject) data;
        
        loaded.forEach((name, stat) -> {
            stats.put((String)name, StatsTable.load((JSONObject)stat));
        });

    }
    
    public synchronized void save() {
        File configFile = new File(plugin.getDataFolder(), "player-stats.json");

        try {
            PrintWriter pw = new PrintWriter(configFile);
            pw.write(this.jsonize().toJSONString());
            pw.flush();
            pw.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    JSONObject jsonize() {
        HashMap<String, JSONObject> result = new HashMap<>();
        stats.forEach((name, stats) -> {
            result.put(name, stats.jsonize());
        });
        return new JSONObject(result);
    }

    TelegramBridge plugin;
    public HashMap<String, StatsTable> stats = new HashMap<>();

    private StatCollector(TelegramBridge plugin) {
        this.plugin = plugin;
        this.load();
        Bukkit.getOnlinePlayers()
            .forEach(player -> getStat(player.getName()).startTimer());
        plugin.commands.addCommand("top", this::getMessage);
    }

    void init() {
        
    }

    void initDataBase() {

    }

    public static StatCollector instance = null;

    public static void initalize(TelegramBridge plugin) {
        
        if (instance != null ) {
            throw new RuntimeException("instance exists already");
        }

        instance = new StatCollector(plugin);
        plugin.getServer()
            .getPluginManager()
            .registerEvents(instance, plugin);
    }

    public static void stop() {
        if (instance != null) {
            instance.update();
            instance.save();
        }
    }

    StatsTable getStat(String name) {
        StatsTable stat = stats.get(name);
        if (stat == null) {
            stat = new StatsTable();
            stats.put(name, stat);
        }
        return stat;
    }


    StatsTable getStatsByEvent(PlayerEvent pevent) {
        String name = pevent.getPlayer().getName();
        return getStat(name);
    }

    StatsTable getStatsByEvent(EntityEvent eevent) {
        String name = eevent.getEntity().getName();
        return getStat(name);
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        getStatsByEvent(event).startTimer();
    }

    @EventHandler
    void onLeft(PlayerQuitEvent event) {
        getStatsByEvent(event).update().stopTimer();
    }

    @EventHandler
    void onDeath(PlayerDeathEvent event) {
        getStatsByEvent(event).deaths++;
    }

    synchronized void update() {
        stats.forEach((ignored_name, stat) -> stat.update());
    }
    
    double getGreatestDeathPeriod() {
        Optional<StatsTable> optable = stats.values().stream()
            .max((a, b) -> (int)(100 * a.getDeathPeriod() - 100 * b.getDeathPeriod()));
        if (optable.isPresent()) {
            return optable.get().getDeathPeriod();
        }
        return 1;
    }

    private class MessageMaker {

        double max_death_period;
        
        MessageMaker() {
            max_death_period = getGreatestDeathPeriod();
        }

        String result = new String("Top 10 players: \n\n");

        <T extends Entry<String, StatsTable> > int comparator(T a, T b) {
            return (int)( b.getValue().total_time - a.getValue().total_time );
        }

        void builder(Entry<String, StatsTable> pair) {
            String player = pair.getKey().replace("`", "\\`");

            StatsTable stat = pair.getValue();
            double alive_coef = stat.getAliveCoefficient(max_death_period);
            
            result += String.format(
                " 🏳️‍🌈 `%s`\n" 
                +"played time — %s\n" + 
                "survival rate — %.3f\n\n", 
                player, stat.toString(), alive_coef 
            );
        }

        void buildUp() {
            stats.entrySet().stream()
                .sorted(this::comparator)
                .limit(10)
                .forEach(this::builder);
        }

        String get() {
            this.buildUp();
            return result;
        }
    }

    public String getMessage() {

        if (stats.size() == 0) { 
            return "no one played yet ...";
        }

        update();
        
        return new MessageMaker().get();
    }

}
