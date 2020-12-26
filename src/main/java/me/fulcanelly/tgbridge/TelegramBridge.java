package me.fulcanelly.tgbridge;

import java.io.File;
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

import lombok.SneakyThrows;

import me.fulcanelly.tgbridge.listeners.ActionListener;
import me.fulcanelly.tgbridge.listeners.TelegramListener;
import me.fulcanelly.tgbridge.tapi.CommandAction;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;
import me.fulcanelly.tgbridge.tools.stats.StatsTable;
import me.fulcanelly.tgbridge.tools.DeepLoger;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.utils.UsefulStuff;
import me.fulcanelly.tgbridge.utils.config.ConfigManager;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;

public class TelegramBridge extends JavaPlugin {

    static TelegramBridge instance = null;

    public ActionListener in_listener;
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

    public TelegramBridge() {
        instance = this;
    }

    public static TelegramBridge getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        if (bot != null ) {
            bot.stop();
        }
        StatCollector.stop();
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

    public static void turnOff() {
        TelegramBridge plugin = getInstance();
        plugin
            .getServer()
            .getPluginManager()
            .disablePlugin(plugin);
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

    private void safeEnable() {
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
          //  CommandEvent
        tgpipe
            .registerListener(new TelegramListener(this));

        
        username = bot
            .getMe()
            .getUsername();


        Message.setBot(bot);
        this.setCommandManager(new CommandManager(username));

        StatCollector.initalize(this);

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
                if (event.args == null) {
                    event.reply("specify nickname to get stats");
                } else {
                    String name = event.args[0];
                    StatsTable stats = StatCollector.instance.stats.get(name);
                    if (stats == null) {
                        event.reply("no players whith such nickname yet");
                    } else {
                        event.reply("you played " + stats.toString());
                    }
                }
            });

        in_listener = new ActionListener(bot, config.chat_id);

        getServer()
            .getPluginManager()
            .registerEvents(in_listener, this);
        
        bot.start();
    }

    void onChatId(Message message) {
        String chat_id = message.getChat()
            .getId()
            .toString();
        message.reply(chat_id);
    }

    class ReloadException extends Exception {

        private static final long serialVersionUID = 1L;
    
    }
    
    void startGuard() {
        try {  
            safeEnable();
        } catch (Throwable e) {
            if (e instanceof ReloadException) {
                startGuard();
            }

            e.printStackTrace();
            turnOff();

        }
    }

    @Override
    @SneakyThrows
    public void onEnable() {    
        new Thread(this::startGuard).start();
    }
}

