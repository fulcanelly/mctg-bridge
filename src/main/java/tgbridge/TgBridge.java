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
import tgbridge.tapi.CommandMatcher;
import tgbridge.tapi.Message;
import tgbridge.tapi.TGBot;
import tgbridge.utils.ConfigLoader;

public class TgBridge extends JavaPlugin {
    static TgBridge instance = null;

    public TgBridge() {
        instance = this;
    }

    public static TgBridge getInstance() {
        return instance;
    }

    class BScheduler extends BukkitRunnable {
        Runnable runnable;

        public BScheduler(Runnable r) {
            this.runnable = r;
        }

        public void run() {
            runnable.run();
        }

        public void schedule(long delay, long period) {
            runTaskTimerAsynchronously(TgBridge.this, delay, period);
        }
    }

    @Override
    public void onDisable() {
    }
    
    public String getMemory()
    {
        final long mb = 1024*1024;
        Runtime rtime = Runtime.getRuntime();
        
        long totalMemory = rtime.totalMemory() / mb;
        long freeMemory = rtime.freeMemory() / mb;
        long usedMemory = totalMemory - freeMemory;

        return String.format("Memory usage: %d MB / %d MB ", usedMemory, totalMemory);
    }

    public String getUptime()
    {
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

    private TGBot bot;

    public List<String> getOnlineList()
    {
        return Bukkit.getOnlinePlayers()
            .stream()
            .map((one) -> one.getName())
            .collect(Collectors.toList());
    }

    interface LocalAnswerer {
        void run(Message msg);
    }

    void enableBot(LocalAnswerer answerer) {
        Message.setBot(bot);
        
        new BScheduler(() -> 
            bot.getLastMessages(json -> 
                answerer.run(new Message(json))
            )
        ).schedule(0L, 30L);
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

    @Override
    public void onEnable() {
        ConfigLoader cLoader = new ConfigLoader("config.json");
        
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

        bot = new TGBot(token);

        try {
            String username = bot
                .getMe()
                .getUsername();
            CommandMatcher.setUsername(username);

        } catch(Exception e) {
            System.out.println("Could not find this bot");
            turnOff();
            return;
        }


        cLoader.getApiToken();

        getServer()
            .getPluginManager()
            .registerEvents(new ActionListener(bot, chat), this);

        enableBot(msg -> {

            if(msg.isText()) {
                String text = msg.getText();
                switch(text) {
                    case "/memory":
                        msg.reply(getMemory());
                        break;
                    case "/uptime":
                        msg.reply(getUptime());
                        break;
                    case "/list":
                        List<String> nick_names = getOnlineList();
                        String result;
                        if(nick_names.size() > 0) {
                            result = "online players: " + String.join("\n", nick_names);
                        } else {
                            result = "Server unfortunately is empty :c";
                        }

                        msg.reply(result);
                    break;
                    case "/chat_id": 

                        String chat_id = msg
                            .getChat()
                            .getId()
                            .toString();
                    
                        msg.reply(chat_id);
                    break;
                    case "/ping":
                        msg.reply("pong");
                    break;
                    default:
                        if(text.startsWith("/")) {
                            msg.reply("unknown command");
                            return;
                        }
                        String name = msg
                            .getFrom()
                            .getName();
                            
                        String answer = String.format(template, name, text);
                        
                        Bukkit.broadcastMessage(answer);
                    break;
                    
                }
            } else if(msg.isVoice()) {
                String name = msg
                    .getFrom()
                    .getName();
                String answer = String.format("* " + template, name, "sent voice.");
                Bukkit.broadcastMessage(answer);
            } else if(msg.isMedia()) {
                String name = msg
                    .getFrom()
                    .getName();
                String answer = String.format("* " + template, name, "sent some media.");
                Bukkit.broadcastMessage(answer);
            }
        });
    }
}

