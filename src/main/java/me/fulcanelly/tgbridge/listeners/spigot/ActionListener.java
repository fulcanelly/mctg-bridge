package me.fulcanelly.tgbridge.listeners.spigot;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEnterBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityMountEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import lombok.Getter;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import com.google.inject.Inject;
import com.saicone.rtag.RtagEntity;

import io.papermc.paper.event.entity.EntityMoveEvent;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.MessageSender;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.UsefulStuff;
import me.fulcanelly.tgbridge.utils.events.pipe.EventReactor;


public class ActionListener implements Listener {

    @Getter
    final MessageSender sender;

    final TGBot bot;
    final Long chatId;
    final SignupLoginReception reception;
    final MainConfig config;

    final Database db = new Database();

    // void osk(Player p) {
    // var perl = p.launchProjectile(EnderPearl.class);
    // // perl.teleport(p)
    // }

    // @EventHandler
    // void dddf(PlayerInteractEvent event) {
    // var player = event.getPlayer();

    // var perl = player.launchProjectile(EnderPearl.class);

    // var location = player.getLocation().clone();
    // location.setX(0);
    // location.setY(300);
    // location.setZ(0);

    // perl.setVelocity(new Vector());
    // perl.teleport(location);

    // }

    @EventHandler
    void onPearlLanding(ProjectileHitEvent event) {
        var oldPearl = event.getEntity();

        if (!oldPearl.getType().equals(EntityType.ENDER_PEARL)) {
            return;
        }

        System.out.println("HIT ! + ! ");
        // var oldPearl = event.getEntity();

        var rtag = new RtagEntity(oldPearl);
        if (rtag.<Object>get("Owner") != null) {
            return;
        }


        var oldPearlUUID = oldPearl.getUniqueId().toString();

        var ownerName = db.getOwnerOfPerl(oldPearlUUID);
        db.removeRecord(oldPearlUUID);

        if (ownerName == null) {
            return;
        }
        ;

        var player = Bukkit.getPlayer(ownerName);

        if (player == null) {
            return;
        }

        // // if config enabled
        // if (!player.getLocation().getWorld().equals(oldPearl.getLocation().getWorld())) {
        //     return;
        // }

        var newPearl = player.launchProjectile(EnderPearl.class);


        newPearl.setVelocity(oldPearl.getVelocity().clone());
        newPearl.teleport(oldPearl.getLocation());
        // player.setCool
        oldPearl.remove();

    }

    @EventHandler
    void onPearlLaunch(ProjectileLaunchEvent event) {
        System.out.println("LAUNCHED ! + !");
        var entity = event.getEntity();

        if (!entity.getType().equals(EntityType.ENDER_PEARL)) {
            return;
        }

        var shooter = entity.getShooter();

        if (shooter instanceof Player player) {
            db.createRecord(player.getName(), entity.getUniqueId().toString());
        }

    }

    // void shit(ProjectileLaunchEvent event) {

    // System.out.println("X");

    // var entity = event.getEntity();

    // if (entity.getType().equals(EntityType.ENDER_PEARL)) {

    // EnderPearl e = (EnderPearl) entity;
    // var shooter = e.getShooter();

    // if (shooter instanceof Player player) {

    // }
    // // player;

    // var rtag = new RtagEntity(entity);

    // // rtag.`
    // System.out.println(rtag.<Object>get("Owner"));
    // System.out.println(rtag.getAttribute("Owner"));

    // System.out.println("PEARL LOUNCH");
    // // System.out.println(rtag.toString());
    // System.out.println(entity.getUniqueId());

    // // System.out.println(rtag.get("any", "Owner").toString());
    // // System.out.println(rtag.get("Owner").toString());

    // }

    // }

    // @EventHandler
    // void loadChunk(ChunkLoadEvent event) {
    // // System.out.println("Abe");

    // var enderEnt = Stream.of(event.getChunk().getEntities())
    // .filter(entity -> entity.getType().equals(EntityType.ENDER_PEARL))
    // .toList();

    // for (var ender : enderEnt) {
    // System.out.println(ender.isInWater());
    // System.out.println(ender.getEntityId());
    // System.out.println(ender.getUniqueId());
    // System.out.println(ender.getName());
    // System.out.println();
    // var rtag = new RtagEntity(ender);
    // // rtag.add("Air", "object" 2233);
    // rtag.set("FFF", "some_field");
    // rtag.add("FFF", "some_field2");

    // rtag.set(23423, "Air");

    // // rtag.update();
    // rtag.load();

    // }

    // }

    // @EventHandler
    // void sd(EntityMoveEvent event) {
    // var entity = event.getEntity();

    // if (entity.getType() == EntityType.ENDER_PEARL) {
    // System.out.println("XXXXX ender");
    // }

    // }

    // @EventHandler
    // public void ok(EntitySpawnEvent event) {
    // var name = event.getEntity().getName();
    // var entity = event.getEntity();
    // var container = entity.getPersistentDataContainer();

    // if (name.contains("Ender")) {
    // for (var x : container.getKeys()) {
    // // container.get(x, null);
    // System.out.println("key:" + x);
    // }
    // System.out.println(event.getEntity().getName());
    // }
    // }

    // void okds(EntityEnterBlockEvent e) {
    // }

    // @EventHandler
    // void playerX(PlayerMoveEvent event) {
    // System.out.println(event);

    // System.out.println("start");

    // var enerEntities = Bukkit.getWorlds().stream()
    // .map(World::getLoadedChunks)
    // .flatMap(Stream::of)

    // .map(Chunk::getEntities)
    // .flatMap(Stream::of)
    // .filter(entity -> entity.getType().equals(EntityType.ENDER_PEARL))
    // .toList();

    // // .toList();

    // System.out.println("end");

    // Bukkit.broadcast("s:" + enerEntities.size(), "*");
    // System.out.println(enerEntities.size());

    // System.out.println("++++++++++++");
    // for (var ender : enerEntities) {
    // // chunk.getEntities();

    // System.out.println(ender.isInWater());
    // System.out.println(ender.getEntityId());
    // System.out.println(ender.getUniqueId());

    // System.out.println(ender.getName());
    // EnderPearl a = null;
    // // a.
    // // a.addPassenger(null);
    // // ender.getType();
    // }
    // System.out.println("---------");

    // ;

    // }

    // void f(EntityJumpEvent e) {
    // // Bukkit.getEntity())
    // }

    // void ok2(EntityMountEvent event) {
    // event.get
    // }

    @Inject
    public ActionListener(
            TGBot bot,
            String chatId,
            MessageSender sender,
            SignupLoginReception reception,
            MainConfig config) {
        this.bot = bot;
        this.chatId = chatId == null ? null : Long.valueOf(chatId);
        this.sender = sender;
        this.reception = reception;
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoing(PlayerJoinEvent event) {
        String player_name = event
                .getPlayer()
                .getName();
        sender.sendNote(
                String.format("`%s` joined the server", player_name));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        String name = event
                .getPlayer()
                .getName();
        sender.sendNote(
                String.format("`%s` left the server", name));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        sender.sendNote(
                UsefulStuff.formatMarkdown(
                        event.getDeathMessage().replaceAll("§\\w", "")));
    }

    // todo
    @EventHandler
    public void onAchivement(PlayerAdvancementDoneEvent event) {

    }

    // forward personal messages to direct messages
    // in telegram if account is bound
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        var list = new ArrayList<String>(
                List.of(event.getMessage().split(" ")));

        if (list.size() < 3) {
            return;
        }
        if (!list.get(0).equals("/w")) {
            return;
        }

        var msg = list.subList(2, list.size())
                .stream().collect(Collectors.joining(" "));

        reception.getTgByUser(list.get(1)).stream()
                .forEach(target -> bot.sendMessage(target, UsefulStuff
                        .formatMarkdown("private message from " + event.getPlayer().getName() + ":\n\n" + msg)));
    }

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        if (!config.enable_chat) {
            return;
        }

        String player_name = event.getPlayer().getName();
        String message = event.getMessage();

        if (event.isCancelled()) {
            return;
        } else {
            sender.sendAsPlayer(player_name, UsefulStuff.formatMarkdown(message));
        }
    }
}
