package tgbridge;

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

import tgbridge.listeners.ActionListener;
import tgbridge.listeners.TelegramListener;
import tgbridge.tapi.Action;
import tgbridge.tapi.CommandManager;
import tgbridge.tapi.Message;
import tgbridge.tapi.TGBot;
import tgbridge.tapi.events.MessageEvent;
import tgbridge.utils.ConfigLoader;
import tgbridge.utils.events.pipe.EventPipe;
import net.md_5.bungee.api.chat.TextComponent;

public class TgBridge extends JavaPlugin {

    static TgBridge instance = null;
    public String username = null;
    public TGBot bot = null;
    public CommandManager commandManager = null;
    public EventPipe tgpipe = null;

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
        final long mb = 1024*1024;
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

        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        return String.format("Uptime : %dh %dm %ds", hours, minutes, seconds);
    }

    public List<String> getOnlineList() {
        return Bukkit.getOnlinePlayers()
            .stream()
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

    @Override
    public void onEnable() {

        ConfigLoader cLoader = new ConfigLoader("config.json");

        //to do: make it shorter
        //BEGIN
        if(!!!cLoader.load()) {
            System.out.println("TgBridge.onEnable(): cant load config file.");
            turnOff();
            return;
        }

        String token = cLoader.getApiToken();
        String chat = cLoader.getPinedChat();

        if(token == null) {
            System.out.println("Can't load bot API token");
            turnOff();
            return;
        }

        if(chat == null) {
            System.out.println("Can't load chat_id");
            turnOff();
            return;
        }
        //END

        tgpipe = new EventPipe();
        TGBot.setEventPipe(tgpipe);

        bot = new TGBot(token);


        //order of adding detecors is important
        bot.getDetectorManager()
            .addDetector(MessageEvent.detector);
            
        tgpipe
            .registerListener(new TelegramListener(this));

        try {
            username = bot
                .getMe()
                .getUsername();
        } catch(Exception e) {
            System.out.println("Could not find this bot");
            turnOff();
            return;
        }

        Message.setBot(bot);
        CommandManager.setUsername(username);
        commandManager = new CommandManager();
        
        commandManager
            .addCommand("ping", msg -> msg.reply("pong"))
            .addCommand("memory", msg -> msg.reply(getMemory()))
            .addCommand("list", getListCmdHandler())
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

