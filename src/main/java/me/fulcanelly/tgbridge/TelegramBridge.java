package me.fulcanelly.tgbridge;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.lang.management.ManagementFactory;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

import me.fulcanelly.tgbridge.listeners.telegram.TelegramListener;
import me.fulcanelly.clsql.container.Pair;
import me.fulcanelly.clsql.container.VirtualConsumer;
import me.fulcanelly.clsql.databse.SQLQueryHandler;
import me.fulcanelly.clsql.stop.StopHandler;
import me.fulcanelly.clsql.stop.Stopable;
import me.fulcanelly.tgbridge.listeners.spigot.ActionListener;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;
import me.fulcanelly.tgbridge.tools.DeepLoger;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.mastery.ChatVisibility;
import me.fulcanelly.tgbridge.utils.UsefulStuff;
import me.fulcanelly.tgbridge.utils.config.ConfigManager;
import me.fulcanelly.tgbridge.utils.database.ConnectionProvider;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;

import me.fulcanelly.tgbridge.view.*;

abstract class MainPluginState extends JavaPlugin implements MainControll {
    
    ActionListener actionListener;
    String username;
    CommandManager commands;

    StopHandler stopHandler = new StopHandler();
    EventPipe tgpipe = new EventPipe();

    String chat_id;

    int secretTempCode;

    MainConfig config;
    ConfigManager<MainConfig> manager;
    SQLQueryHandler queryHandler;


    
    void setUpConfig() {
        config = new MainConfig(); 
        manager = new ConfigManager<>(config, this);
        manager.load();
    }

    public SQLQueryHandler getSQLQueryHandler() {
        return queryHandler;
    }

    void setUpSQLhandler() {
        var conn = new ConnectionProvider(this)
            .getConnection();
        queryHandler = new SQLQueryHandler(conn);
    }

    public ActionListener getActionListener() {
        return actionListener;
    }
    
    public EventPipe getTelegramPipe() {
        return tgpipe;
    }

    public CommandManager getCommandManager() {
        return commands;
    }

    void setCommandManager(CommandManager manager) {
        this.commands = manager;
    }

    public String getPinnedChatId() {
        return chat_id;
    }

    Random random = new Random();

    public synchronized int generateSecretTempCode() {
        secretTempCode = random.nextInt() % 100000;
        this.getLogger().info( 
            ChatColor.GREEN + "secretTempCode is set to " + secretTempCode);
        return secretTempCode;
    }

}

public class TelegramBridge extends MainPluginState {
    
    @Override
    public void onDisable() {
        tlog.sendToPinnedChat("plugin stoped");
        stopHandler.stopAll();
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

    Consumer<CommandEvent> getListCmdHandler() {
        String emptyServerMessage = "Server unfortunately is empty :c";

        return msg -> {
            List<String> nick_names = getOnlineList();

            String result = nick_names.size() > 0 ? 
                "Currently there are " + nick_names.size() + " players online: \n\n" + String.join("\n", nick_names):
                emptyServerMessage;
            msg.reply(result);
        };
    }

    void regStopHandlers(Stopable ...more) {
        Arrays.asList(more).forEach(stopHandler::register);
    }

    class TelegramLogger {

        final TGBot bot;
        
        TelegramLogger(TGBot bot) {
            this.bot = bot;
        }

        void sendToPinnedChat(String text) {
            if (config.getChatId() != null && bot != null) {
                bot.sendMessage(Long.valueOf(chat_id), text);
            }
        }
    }

    TelegramLogger tlog;

    private void safeEnable() throws ReloadException {
        this.setUpConfig();
        this.setUpSQLhandler();

        DeepLoger.initalize(this);
        
        chat_id = config.getChatId();

        StatCollector statCollector = new StatCollector(this.getSQLQueryHandler()); //
        TGBot bot = new TGBot(config.getApiToken(), tgpipe);
        
        tlog = new TelegramLogger(config.log_status ? bot : null);
        
        //order of adding detectors is important
        bot.getDetectorManager()
            .addDetector(MessageEvent.detector);

        tgpipe
            .registerListener(new TelegramListener(this));
        
        username = bot
            .getMe()
            .getUsername();

        this.setCommandManager(new CommandManager(username));
        
        this.generateSecretTempCode();
        
        if (config.getChatId() == null) {
            this.getLogger().warning(
                "chat_id is null, use /attach <secretTempCode> to pin one");
        }

        this.regTelegramCommands(manager, config, statCollector);

        actionListener = new ActionListener(bot, config.getChatId());
        
        this.regSpigotListeners(actionListener, statCollector);
        this.regStopHandlers(tgpipe, queryHandler, bot);

        this.regCommandAndTabCompleters(new ChatVisibility());

        tlog.sendToPinnedChat("plugin started");
        bot.start();
    }

    void regCommandAndTabCompleters(NamedTabExecutor... executors) {
        Stream.of(executors)
            .map(one -> new Pair<>(one, this.getCommand(one.getCommandName())))
            .forEach(pair -> new VirtualConsumer<NamedTabExecutor>(pair.second::setExecutor)
                .andThen(pair.second::setTabCompleter)
                .accept(pair.first)
            );
    }

    void regSpigotListeners(Listener ...listeners) {
        Arrays.asList(listeners)
            .forEach(listener -> this.getServer()
                .getPluginManager()
                .registerEvents(listener, this)
            );
    }

    void regTelegramCommands(ConfigManager<MainConfig> manager, MainConfig config, StatCollector statCollector) {
        commands
            .addCommand("attach", event -> {
                
                System.out.println("attach: " + event.getArgs());
                if (event.getArgs().isEmpty()) {
                    return "Sepcify secret code";
                } else if (Integer.toString(secretTempCode).equals(event.args[0])) {
                    config.setChatId(event.getChat().getId());
                    manager.save();
                    this.generateSecretTempCode();
                    return "OK, done. Reload plugin";
                } else {
                    return "Wrong code";
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
                    var nick = event.args[0];
                    
                    var optStats = statCollector
                        .findByName(nick)
                        .waitForResult();
                                    
                    if (optStats.isEmpty()) {
                        event.reply("no players whith such nickname yet");
                    } else {
                        var stats = optStats.get();
                        
                        String online_sign = Bukkit.getPlayer(stats.name) != null ? "❇️" : "";

                        String data = String.format(
                            " 🏳️‍🌈 `%s` " + online_sign + '\n' +
                            "  played time — %s\n" + 
                            "  deaths — %d\n",
                            stats.name, stats.toString(), stats.deaths
                        );
    
                        event.reply(data);
                    }
                }
            })
            .addCommand("top", statCollector::getMessage);
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

