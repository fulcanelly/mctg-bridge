package me.fulcanelly;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.json.simple.JSONObject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.MainConfig;
import me.fulcanelly.tgbridge.tools.mastery.ChatSettings;
import me.fulcanelly.tgbridge.tools.twofactor.InGameReceptionUI;

public class BaseMocksModule extends AbstractModule {

    public final static Long CHAT_ID = 12345l;

    @Provides
    InGameReceptionUI getInGameReceptionUI() {
        return mock(InGameReceptionUI.class);
    }

    @Provides
    ChatSettings getChatSettings() {
        return mock(ChatSettings.class);
    }

    @Provides
    @Singleton
    TGBot getTgBot() {

        var tg = mock(TGBot.class);

        doAnswer((args) -> {
            Random rand = new Random();
            JSONObject object = new JSONObject(Map.of("message_id", rand.nextLong()));
            return new Message(object, getTgBot());
        })
                .when(tg)
                .sendMessage(anyLong(), anyString());

        doAnswer((args) -> {
            Random rand = new Random();
            JSONObject object = new JSONObject(Map.of("message_id", rand.nextLong()));
            return new Message(object, getTgBot());
        })
                .when(tg)
                .editMessage(anyLong(), anyLong(), anyString());

        return tg;
    }

    @Provides
    MainConfig provideConfig() {
        var config = new MainConfig();

        config.chat_id = CHAT_ID.toString();
        config.enable_chat = true;

        return config;
    }
}
