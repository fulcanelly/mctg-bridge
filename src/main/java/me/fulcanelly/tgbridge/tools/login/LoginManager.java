package me.fulcanelly.tgbridge.tools.login;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import me.fulcanelly.tgbridge.TgBridge;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.events.MessageEvent;
import me.fulcanelly.tgbridge.utils.events.pipe.EventReactor;
import me.fulcanelly.tgbridge.utils.events.pipe.Listener;
import net.md_5.bungee.api.ChatColor;

class LoginDataBase {
    boolean includes(Player pl) { 
        return false;
    }
    boolean isCached(InetAddress adr) {
        return false;
    }
    String getChatID(String nick) {
        return null;
    }
}

@interface todo { }

public class LoginManager implements org.bukkit.event.Listener, CommandExecutor {

    static TelegramInterface instance;

    public Listener getListener() {
        if (instance == null ) {
            instance = new TelegramInterface();
        }
        return instance;
    }

    class TelegramInterface implements Listener {

        @EventReactor
        void onMessage(MessageEvent event) {
            String code = event.getText();
            if (code == null) {
                return;
            }
            String player = registration.get(code);
            if (player == null) {
                return;
            };
            LoginPlayerData playerdata = players.get(player);
            if (playerdata == null) {
                return;
            }
            playerdata.allowAllActions();
            playerdata.player.sendMessage("You are successfully logined");
        }

        Message sendLoginKey(String who, String key) {
            String chat_id = login_db.getChatID(who);
            return bridge.bot.sendMessage(
                new Long(chat_id), "Your login key is `" + key + "`"
            );
        }

        /*@me.fulcanelly.tgbridge.utils.events.pipe.EventHandler
        void onMessage2(MessageEvent event) {
            try {
                String code = event.getText();
                String player = registration.get(code);
                LoginPlayerData playerdata = players.get(player);

                playerdata.allowAllActions();
            } catch(NullPointerException ignored) {}
        }*/
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return false;
        }
        System.out.println(command);
        System.out.println(label);
        System.out.println(args);
        if (sender instanceof Player) {
            Player player = (Player)sender;
            player.setGameMode(GameMode.SPECTATOR);
        }
        return true;

    }   

    TgBridge bridge;
    //key , player
    HashMap<String, String> registration = new HashMap<>();
    //
    HashMap<String, String> logining = new HashMap<>();

    public LoginManager(TgBridge bridge) {
        this.bridge = bridge;
    }

    LoginDataBase login_db = new LoginDataBase();
    Set<String> blocked = new LinkedHashSet<>();
    HashMap<String, LoginPlayerData> players = new HashMap<>(); 

    class LoginPlayerData {
        
        Player player;
        InetAddress iaddr;
        String name;

        LoginPlayerData(PlayerLoginEvent event) {
            player = event.getPlayer();
            iaddr = event.getAddress();
            name = player.getName();

            blocked.add(name);
            players.putIfAbsent(name, this);
        }

        boolean isExists() { 
            return login_db.includes(player);
        }

        boolean isCached() {
            return false;
        }

        private int rangeRand(int from, int to) {
            int diff = to - from + 1;
            double res = Math.random() * diff;
            return (int)(from + res);
        }

        private String generateKey() {
            return Integer.toString(rangeRand(1000, 9999));
        }

        void sendRegisterOffer( String key) {
            player.sendMessage(ChatColor.RED + "To play on server you have to be registered");
            player.sendMessage(
                ChatColor.WHITE + "Send " + ChatColor.BLUE + key + ChatColor.WHITE + " to " + bridge.username + " to register"
            );
        }

        void setUpRegistartion() {
            String key = generateKey();
            sendRegisterOffer(key);
            registration.put(key, name);
        }

        //@todo         
        void blockAllActions() {
            player.setGameMode(GameMode.SPECTATOR);
            blocked.add(name);
        } 

        //@todo
        void allowAllActions() {
            player.setGameMode(GameMode.SURVIVAL);
            blocked.remove(name);
        }
        
        @todo         
        void sendDisposablePassword() {

        }

        @todo
        void waitForLogin() {
            sendDisposablePassword();
        }

    }
    
    void checkEvent(Cancellable event) {
        PlayerEvent player_event = (PlayerEvent)event;
        String name = player_event.getPlayer().getName();

        if (blocked.contains(name)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void chatBlocker(AsyncPlayerChatEvent event) {
        checkEvent(event);
    }

    @EventHandler
    void onMovement(PlayerMoveEvent event) {
        checkEvent(event);
    }

    @EventHandler
    void onQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        blocked.remove(name);
        inGamePlayers.remove(name);
    }

    @EventHandler
    void onSpawn(PlayerSpawnLocationEvent event) {

        String playerName = event.getPlayer().getName();
        LoginPlayerData player = inGamePlayers.get(playerName);
        // BukkitScheduler.
        player.blockAllActions();
        if (!player.isExists()) {
            System.out.println("aaa");
            player.setUpRegistartion();
            return;
        }

        if (player.isCached()) {
            player.allowAllActions();
        } else {
            player.waitForLogin();
        }
    }
     
    HashMap<String, LoginPlayerData> inGamePlayers = new HashMap<>();

    @EventHandler
    void onLogin(PlayerLoginEvent event) {
        LoginPlayerData player = new LoginPlayerData(event);
        inGamePlayers.put(player.name, player);


      

     //   System.out.println( event.getAddress() );
    //    System.out.println( event.getPlayer().getAddress() );

    }

   // void on(PlayerRespawnEvent)
   // void onLogin(E)
}
