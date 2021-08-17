package me.fulcanelly.tgbridge.tools;

import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tapi.TGBot;

public class TelegramLogger {

    /**
     *
     */
    final TGBot bot;
    final MainConfig config;

    @Inject
    public TelegramLogger(TGBot bot, MainConfig config) {
        this.bot = bot;
        this.config = config;
    }

    public void sendToPinnedChat(String text) {
        if (config.getChatId() != null && bot != null) {
            bot.sendMessage(Long.valueOf(config.getChatId()), text);
        }
    }
}