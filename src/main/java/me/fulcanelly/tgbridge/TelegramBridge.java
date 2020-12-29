package me.fulcanelly.tgbridge;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.lang.management.ManagementFactory;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

import me.fulcanelly.tgbridge.listeners.ActionListener;
import me.fulcanelly.tgbridge.listeners.TelegramListener;
import me.fulcanelly.tgbridge.tapi.CommandAction;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;
import me.fulcanelly.tgbridge.tools.DeepLoger;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.utils.UsefulStuff;
import me.fulcanelly.tgbridge.utils.config.ConfigManager;
import me.fulcanelly.tgbridge.utils.databse.ConnectionProvider;
import me.fulcanelly.tgbridge.utils.databse.LazySQLActor;
import me.fulcanelly.tgbridge.utils.databse.QueryHandler;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;

public class TelegramBridge extends JavaPlugin {

    LazySQLActor sqlhandler;

    public LazySQLActor getSQLhandler() {
        return sqlhandler;
    }
    
    void setUpSQLhandler() {
        var conn = new ConnectionProvider(this)
            .getConnection();
    
        sqlhandler = new LazySQLActor(new QueryHandler(conn));
    }
    
    public ActionListener actionListener;
    public String username = null;
    public TGBot bot = null;
    public CommandManager commands = null;
    public EventPipe tgpipe = new EventPipe();

    
    CommandManager getCommandsManager() {
        return commands;
    }

    void setCommandManager(CommandManager manager) {
        this.commands = manager;
    }

    String chat_id;

    public String getPinnedChatId() {
       return chat_id;
    }


    @Override
    public void onDisable() {
        getServer()
            .getOnlinePlayers()
            //.parallelStream()
            .forEach(player -> player.kickPlayer("Server closed."));
            
        if (bot != null ) {
            bot.stop();
        }
    }
    
    public String getMemory() {
        final long mb = 1024 * 1024;
        Runtime rtime = Runtime.getRuntime();
        
        long totalMemory = rtime.totalMemory() / mb;
        long freeMemory = rtime.freeMemory() / mb;
        long usedMemory = totalMemory - freeMemory;

        return String.format("Memory usage: %d MB / %d MB ", usedMemory, totalMemory);
    }

    public String getUptime() {
        long jvmUpTime = ManagementFactory
            .getRuntimeMXBean()
            .getUptime();

        Calendar calendar = GregorianCalendar   
            .getInstance();

        calendar.setTime(new Date(jvmUpTime));
        
        int day = calendar.get(Calendar.DAY_OF_YEAR) - 1;
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        return String.format("Uptime : %dd %dh %dm %ds", day,  hours, minutes, seconds);
    }

    public List<String> getOnlineList() {
        return Bukkit.getOnlinePlayers().stream()
            .map(player -> player.getName())
            .map(username -> UsefulStuff.formatMarkdown(username))
            .collect(Collectors.toList());
    }

    public void turnOff() {
        this
            .getServer()
            .getPluginManager()
            .disablePlugin(this);
    }
    
    int secretTempCode;
    
    void generateSecretTempCode() {
        secretTempCode = new Random().nextInt() % 100000;
        this.getLogger().info( 
            ChatColor.GREEN + "secretTempCode is set to " + secretTempCode);
    }

    CommandAction getListCmdHandler() {
        String emptyServerMessage = "Server unfortunately is empty :c";

        return msg -> {
            List<String> nick_names = getOnlineList();

            String result = nick_names.size() > 0 ? 
                "`Online players:` \n\n" + String.join("\n", nick_names):
                emptyServerMessage;
            msg.reply(result);
        };
    }

    private void safeEnable() throws ReloadException {
        this.setUpSQLhandler();

        DeepLoger.initalize(this);
        TGBot.setEventPipe(tgpipe);

        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }

        var config = new MainConfig(); 

        new ConfigManager<>(config, this.getDataFolder()).load();

        chat_id = config.chat_id;

        bot = new TGBot(config.api_token);

        //order of adding detectors is important
        bot.getDetectorManager()
            .addDetector(MessageEvent.detector);

        tgpipe
            .registerListener(new TelegramListener(this));
        
        username = bot
            .getMe()
            .getUsername();

        Message.setBot(bot);
        this.setCommandManager(new CommandManager(username));

        StatCollector statCollector = new StatCollector(this); 
        generateSecretTempCode();
        
        if (config.chat_id == null) {
            this.getLogger().warning(
                "chat_id is null, use /attach <secretTempCode> to pin one");
        }

        commands
            .addCommand("attach", event -> {
                if (event.getArgs().isEmpty()) {
                    event.reply("Sepcify secret code");

                } else if (Integer.parseInt(event.args[0]) == secretTempCode) {
                    config.chat_id = event.getChat().getId().toString();
                    event.reply("OK, done. Reload plugin");
                    new ConfigManager<>(config, this.getDataFolder()).save();
                    this.generateSecretTempCode();
                } else {
                    event.reply("Wrong code");
                }
            })
            .addCommand("ping", "pong")
            .addCommand("memory", this::getMemory)
            .addCommand("list", this.getListCmdHandler())
            .addCommand("chat_id", this::onChatId)
            .addCommand("uptime", this::getUptime)
            .addCommand("stats", event -> {    
                if (event.getArgs().isEmpty()) {
                    event.reply("specify nickname to get stats");
                } else {
                    var stats = statCollector
                        .findByName(event.args[0]);

                    if (stats.isEmpty()) {
                        event.reply("no players whith such nickname yet");
                    } else {
                        event.reply("you played " + stats.get().toString());
                    }
                }
            });

        actionListener = new ActionListener(bot, config.chat_id);
        
        Arrays.asList(actionListener, statCollector)
            .forEach(listener -> {
                getServer()
                    .getPluginManager()
                    .registerEvents(listener, this);
            });

        bot.start();
    }

    void onChatId(Message message) {
        String chat_id = message.getChat()
            .getId()
            .toString();
        message.reply(chat_id);
    }


    
    void startGuard() {
        try {  
            safeEnable();
        } catch(ReloadException e) {
            startGuard();
        } catch (Throwable e) {
            e.printStackTrace();
            turnOff();
        }
    }

    @Override
    public void onEnable() {    
        new Thread(this::startGuard).start();
    }
}

class ReloadException extends Exception {

    private static final long serialVersionUID = 1L;

}