package me.fulcanelly.tgbridge;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.fulcanelly.tgbridge.listeners.ActionListener;
import me.fulcanelly.tgbridge.listeners.TelegramListener;
import me.fulcanelly.tgbridge.tapi.CommandAction;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.tools.login.LoginManager;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;
import me.fulcanelly.tgbridge.utils.DeepLoger;
import me.fulcanelly.tgbridge.utils.MainConfig;
import me.fulcanelly.tgbridge.utils.UsefulStuff;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;

class BScheduler extends BukkitRunnable {
    Runnable runnable;

    public BScheduler(Runnable r) {
        this.runnable = r;
    }

    public void run() {
        runnable.run();
    }

    public void schedule(long delay, long period) {
        runTaskTimerAsynchronously(TgBridge.getInstance(), delay, period);
    }
}

public class TgBridge extends JavaPlugin {

    static TgBridge instance = null;

    public ActionListener in_listener;
    public String username = null;
    public TGBot bot = null;
    public CommandManager commands = null;
    public EventPipe tgpipe = new EventPipe();

    public MainConfig config;
    public String chat;

    public TgBridge() {
        instance = this;
    }

    public static TgBridge getInstance() {
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
        TgBridge plugin = getInstance();
        plugin
            .getServer()
            .getPluginManager()
            .disablePlugin(plugin);
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

    @Override
    public void onEnable() {
        DeepLoger.initalize(this);

        if (!new File(getDataFolder(), "config.json").exists()) {
            saveResource("config.json", false);
        }

        try {
            config = new MainConfig(this);
            config.manager.load();
        } catch(Exception e) {
            getLogger()
                .warning(e.getMessage());
            turnOff();
            return;
        }

        chat = config.chat_id;

        LoginManager loginer = new LoginManager(this);

        if (config.login_manger) {
            getServer()
                .getPluginManager()
                .registerEvents(loginer, this);

            getCommand("log").setExecutor(loginer);

            //System.out.println(loginer.getListener());
            // tgpipe
            //   .registerListener(loginer.getListener());
        }

        TGBot.setEventPipe(tgpipe);

        bot = new TGBot(config.api_token);

        //order of adding detectors is important
        bot.getDetectorManager()
            .addDetector(MessageEvent.detector);
            
        tgpipe
            .registerListener(new TelegramListener(this));

        try {
            username = bot
                .getMe()
                .getUsername();
        } catch(Exception e) {
            e.printStackTrace();

            turnOff();
            return;
        }

        Message.setBot(bot);
        CommandManager.setUsername(username);
        commands = new CommandManager();
        StatCollector.initalize(this);

        commands
            .addCommand("ping", msg -> msg.reply("pong"))
            .addCommand("memory", msg -> msg.reply(getMemory()))
            .addCommand("list", this.getListCmdHandler())
            .addCommand("chat_id", this::onChatId)
            .addCommand("uptime", msg -> msg.reply(getUptime()));
        
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
}

