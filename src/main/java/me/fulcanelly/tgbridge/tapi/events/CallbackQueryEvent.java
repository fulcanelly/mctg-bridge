package me.fulcanelly.tgbridge.tapi.events;

import org.json.simple.JSONObject;

import me.fulcanelly.tgbridge.tapi.From;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.TGBot;

public class CallbackQueryEvent {

    public JSONObject callbackQuery = new JSONObject();

    TGBot bot;

    public <T> CallbackQueryEvent(T callbackQuery, TGBot bot) {
        this.bot = bot;
        this.callbackQuery = (JSONObject) callbackQuery;
    }

    public String getId() {
        return (String) callbackQuery.get("id");
    }

    public String getData() {
        return (String) callbackQuery.get("data");
    }

    public From getFrom() {
        return new From(callbackQuery.get("from"));
    }

    public Message getMessage() {
        return new Message(callbackQuery.get("message"), bot);
    }

    public void answer(String text) {
        bot.answerCallbackQuery(getId(), text);
    }
}
