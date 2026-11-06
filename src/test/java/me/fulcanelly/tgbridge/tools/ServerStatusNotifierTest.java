package me.fulcanelly.tgbridge.tools;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;

public class ServerStatusNotifierTest {

    TelegramLogger tlog = mock(TelegramLogger.class);
    TGBot bot = mock(TGBot.class);
    SignupLoginReception reception = mock(SignupLoginReception.class);
    Server server = mock(Server.class);
    MainConfig config = new MainConfig();

    ServerStatusNotifier notifier;

    @BeforeEach
    void setUp() {
        notifier = new ServerStatusNotifier(tlog, bot, config, reception, server, mock(Logger.class));
    }

    OfflinePlayer op(String name) {
        var player = mock(OfflinePlayer.class);
        when(player.getName()).thenReturn(name);
        return player;
    }

    // build mocks before stubbing: Mockito can't stub inside another stubbing call
    void operators(String... names) {
        var players = new java.util.ArrayList<OfflinePlayer>();
        for (var name : names) {
            players.add(op(name));
        }
        var set = new java.util.HashSet<OfflinePlayer>(players);
        when(server.getOperators()).thenReturn(set);
    }

    @Test
    void statusMessagesCarryEmoji() {
        notifier.notifyStarted();
        notifier.notifyStopped();

        verify(tlog).sendToPinnedChat("✅ Plugin started");
        verify(tlog).sendToPinnedChat("❌ Plugin stopped");
    }

    @Test
    void enabledByDefault() {
        assertTrue(config.isNotifyAdminServerStatusEnabled());
    }

    @Test
    void sendsPrivateMessageToLinkedOperatorsOnly() {
        operators("alice", "bob");
        when(reception.getTgByUser("alice")).thenReturn(Optional.of(111L));
        when(reception.getTgByUser("bob")).thenReturn(Optional.empty());

        notifier.notifyStarted();

        verify(bot).sendMessage(111L, "✅ Plugin started");
        verify(bot, times(1)).sendMessage(anyLong(), anyString());
    }

    @Test
    void telegramAccountLinkedToSeveralOperatorsIsNotifiedOnce() {
        operators("alice", "alice_alt");
        when(reception.getTgByUser("alice")).thenReturn(Optional.of(111L));
        when(reception.getTgByUser("alice_alt")).thenReturn(Optional.of(111L));

        notifier.notifyStopped();

        verify(bot, times(1)).sendMessage(111L, "❌ Plugin stopped");
    }

    @Test
    void operatorWithoutKnownNameIsSkipped() {
        operators((String) null);

        notifier.notifyStarted();

        verify(bot, never()).sendMessage(anyLong(), anyString());
    }

    @Test
    void disabledOptionSkipsPrivateMessagesButKeepsGroupLog() {
        config.notify_admin_server_status = false;
        operators("alice");
        when(reception.getTgByUser("alice")).thenReturn(Optional.of(111L));

        notifier.notifyStarted();

        verify(tlog).sendToPinnedChat("✅ Plugin started");
        verify(bot, never()).sendMessage(anyLong(), anyString());
    }

    @Test
    void failedDeliveryToOneAdminDoesNotBlockOthers() {
        operators("alice", "bob");
        when(reception.getTgByUser("alice")).thenReturn(Optional.of(111L));
        when(reception.getTgByUser("bob")).thenReturn(Optional.of(222L));
        when(bot.sendMessage(111L, "✅ Plugin started")).thenThrow(new RuntimeException("boom"));

        notifier.notifyStarted();

        verify(bot).sendMessage(111L, "✅ Plugin started");
        verify(bot).sendMessage(222L, "✅ Plugin started");
    }
}
