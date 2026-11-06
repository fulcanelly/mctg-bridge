package me.fulcanelly.tgbridge.tools;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;

/**
 * Reports plugin start / stop status.
 *
 * <ul>
 * <li>to the pinned telegram chat (controlled by {@code log_status}, via {@link TelegramLogger})</li>
 * <li>as a private message to every server operator that has a linked telegram
 * account (controlled by {@code notify_admin_server_status})</li>
 * </ul>
 */
public class ServerStatusNotifier {

    public static final String STARTED = "✅ Plugin started";
    public static final String STOPPED = "❌ Plugin stopped";

    final TelegramLogger tlog;
    final TGBot bot;
    final MainConfig config;
    final SignupLoginReception reception;
    final Server server;
    final Logger logger;

    @Inject
    public ServerStatusNotifier(
            TelegramLogger tlog,
            TGBot bot,
            MainConfig config,
            SignupLoginReception reception,
            Server server,
            @Named("spigot.logger") Logger logger) {
        this.tlog = tlog;
        this.bot = bot;
        this.config = config;
        this.reception = reception;
        this.server = server;
        this.logger = logger;
    }

    public void notifyStarted() {
        notifyAll(STARTED);
    }

    public void notifyStopped() {
        notifyAll(STOPPED);
    }

    private void notifyAll(String status) {
        try {
            tlog.sendToPinnedChat(status);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to send status to the telegram chat", e);
        }

        if (config.isNotifyAdminServerStatusEnabled()) {
            notifyAdmins(status);
        }
    }

    /**
     * Telegram accounts of all operators.
     *
     * An operator has at most one linked telegram account (registration refuses
     * a player that is already bound), while one telegram account may be linked to
     * several operators - hence the set, so nobody gets the same message twice.
     */
    Set<Long> findAdminTelegramIds() {
        var ids = new LinkedHashSet<Long>();

        for (OfflinePlayer operator : server.getOperators()) {
            var name = operator.getName();
            if (name != null) {
                reception.getTgByUser(name).ifPresent(ids::add);
            }
        }

        return ids;
    }

    private void notifyAdmins(String status) {
        Set<Long> targets;

        try {
            targets = findAdminTelegramIds();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to look up admins' telegram accounts", e);
            return;
        }

        for (var target : targets) {
            try {
                bot.sendMessage(target, status);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to send status to telegram user " + target, e);
            }
        }
    }
}
