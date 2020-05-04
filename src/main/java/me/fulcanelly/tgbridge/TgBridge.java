package me.fulcanelly.tgbridge;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.fulcanelly.tgbridge.listeners.ActionListener;
import me.fulcanelly.tgbridge.listeners.TelegramListener;
import me.fulcanelly.tgbridge.tapi.Action;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.utils.ConfigLoader;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;

public class TgBridge extends JavaPlugin {

    static TgBridge instance = null;
    public String username = null;
    public TGBot bot = null;
    public CommandManager commandManager = null;
    public EventPipe tgpipe = null;

    String token = null;
    String chat = null;
    ConfigLoader cLoader = null;

    public TgBridge() {
        instance = this;
    }

    public static TgBridge getInstance() {
        return instance;
    }

    static public class BScheduler extends BukkitRunnable {
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

    @Override
    public void onDisable() {
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
            .map((one) -> one.getName())
            .collect(Collectors.toList());
    }

    final String template = 
        ChatColor.BLUE + "[tg]" +
        ChatColor.YELLOW + "[%s]" +
        ChatColor.RESET + " %s";

    public static void turnOff() {
        TgBridge plugin = getInstance();
        plugin
            .getServer()
            .getPluginManager()
            .disablePlugin(plugin);
    }

    Action getListCmdHandler() {
        String emptyServerMessage = "Server unfortunately is empty :c";

        return msg -> {
            List<String> nick_names = getOnlineList();
            String result = nick_names.size() > 0 ? 
                "Online players: " + String.join("\n", nick_names) :
                emptyServerMessage;
            msg.reply(result);
        };
    }

    void loadConfig() throws Exception {
        cLoader = new ConfigLoader("config.json");
        if(!cLoader.load()) {
            throw new Exception("TgBridge.onEnable(): cant load config file.");
        }
    }

    void obtainToken() throws Exception {
        token = cLoader.getApiToken();

        if(token == null) {
            throw new Exception("Can't load bot API token");
        }
    }

    void obtainChat() throws Exception {
        chat = cLoader.getPinnedChat();
        if(chat == null) {
            throw new Exception("Can't load chat_id");
        }
    }

    @Override
    public void onEnable() {
        if (!new File(getDataFolder(), "config.json").exists()) {
            saveResource("config.json", false);
        }

        try {
            loadConfig();
            obtainChat();
            obtainToken();
        } catch(Exception e) {
            getLogger()
                .warning(e.getMessage());
            turnOff();
            return;
        }

        tgpipe = new EventPipe();
        TGBot.setEventPipe(tgpipe);
        bot = new TGBot(token);

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
            getLogger()
                .warning("Could not find this bot");
            turnOff();
            return;
        }

        Message.setBot(bot);
        CommandManager.setUsername(username);
        commandManager = new CommandManager();
        
        commandManager
            .addCommand("ping", msg -> msg.reply("pong"))
            .addCommand("memory", msg -> msg.reply(getMemory()))
            .addCommand("list", this.getListCmdHandler())
            .addCommand("chat_id", msg -> {
                String chat_id = msg
                    .getChat()
                    .getId()
                    .toString();
        
                msg.reply(chat_id);
            })
            .addCommand("uptime", msg -> msg.reply(getUptime()));
            

        cLoader.getApiToken();

        getServer()
            .getPluginManager()
            .registerEvents(new ActionListener(bot, chat), this);
        
        
        bot.start();
    }
}

