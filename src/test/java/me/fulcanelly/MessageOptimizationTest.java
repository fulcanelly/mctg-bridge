package me.fulcanelly;

import lombok.SneakyThrows;
import me.fulcanelly.tgbridge.listeners.spigot.ActionListener;
import me.fulcanelly.tgbridge.tapi.TGBot;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.Test;

public class MessageOptimizationTest extends BaseTest {
    @Inject
    ActionListener listener;

    @Inject
    TGBot bot;

    Player getPlayerMock(String name) {
        var player = mock(Player.class);
        when(player.getName()).thenReturn(name);

        return player;
    }

    Player getPlayerMock() {
        return getPlayerMock("mockplayer");
    }

    @Test
    @SneakyThrows
    void shouldConcatMessages() {
        listener.onChatEvent(
                new AsyncPlayerChatEvent(false, getPlayerMock(), "test", null));

        listener.onChatEvent(
                new AsyncPlayerChatEvent(false, getPlayerMock(), "test", null));

        System.out.println("testShouldConcatMessages");

        System.out.println(bot);

        Thread.sleep(100);

        var boti = inOrder(bot);
        boti.verify(bot).sendMessage(eq(BaseMocksModule.CHAT_ID), eq("*<mockplayer>* test"));

        boti.verify(bot)
                .editMessage(
                        eq(BaseMocksModule.CHAT_ID), anyLong(),
                        eq("*<mockplayer>*\n" +
                                "test\n\n" +
                                "test"));
    }

    @SneakyThrows
    @Test
    void shouldDedupliacteMessages() {
        listener.onPlayerJoing(
                new PlayerJoinEvent(getPlayerMock(), "join message"));
        listener.onPlayerJoing(
                new PlayerJoinEvent(getPlayerMock(), "join message"));
        listener.onPlayerJoing(
                new PlayerJoinEvent(getPlayerMock(), "join message"));

        Thread.sleep(100);

        var boti = inOrder(bot);

        boti.verify(bot).sendMessage(eq(BaseMocksModule.CHAT_ID), eq("`mockplayer` joined the server"));
        boti.verify(bot)
                .editMessage(
                        eq(BaseMocksModule.CHAT_ID), anyLong(),
                        eq(
                                "\n" +
                                        " # repeats 2 times \n\n" +
                                        "`mockplayer` joined the server"

                        ));

        boti.verify(bot)
                .editMessage(
                        eq(BaseMocksModule.CHAT_ID), anyLong(),
                        eq(
                                "\n" +
                                        " # repeats 3 times \n\n" +
                                        "`mockplayer` joined the server"

                        ));
    }

    @Test
    @SneakyThrows
    void shouldDeduplicateRepeatingSequences() {
        listener.onPlayerJoing(
                new PlayerJoinEvent(getPlayerMock(), null));

        listener.onPlayerLeave(new PlayerQuitEvent(getPlayerMock(), null));

        listener.onPlayerJoing(
                new PlayerJoinEvent(getPlayerMock(), null));

        listener.onPlayerLeave(new PlayerQuitEvent(getPlayerMock(), null));

        Thread.sleep(100);

        var boti = inOrder(bot);

        boti.verify(bot).sendMessage(eq(BaseMocksModule.CHAT_ID), eq("`mockplayer` joined the server"));
        boti.verify(bot)
                .editMessage(
                        eq(BaseMocksModule.CHAT_ID), anyLong(),
                        eq("\n\n" +
                                "`mockplayer` joined the server\n" +
                                "`mockplayer` left the server"));

        boti.verify(bot)
                .editMessage(
                        eq(BaseMocksModule.CHAT_ID), anyLong(),
                        eq("\n\n" +
                                "`mockplayer` joined the server\n" +
                                "`mockplayer` left the server\n" +
                                "`mockplayer` joined the server"));

        boti.verify(bot)
                .editMessage(
                        eq(BaseMocksModule.CHAT_ID), anyLong(),
                        eq("\n" +
                                " # repeats 2 times \n\n" +
                                "`mockplayer` joined the server\n" +
                                "`mockplayer` left the server"));

    }

    @Test
    void shouldDeduplicateMisxedEvents() {

    }

}
