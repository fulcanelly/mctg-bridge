package me.fulcanelly.tgbridge.tools.compact.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.compact.message.CompactableMessage;
import me.fulcanelly.tgbridge.tools.compact.message.PlayerMessage;

@Getter
public class SignedMessageCtx extends CompactionContext {

    String from;
    String text;

    public SignedMessageCtx(TGBot bot, Long chatID, String from, String text) {
        super(bot, chatID);
        this.from = from;
        this.text = text;
    }

    @Override
    public CompactableMessage send() {
        return new PlayerMessage(
                bot.sendMessage(chatID, String.format("*<%s>* %s", from, text)).getMsgId(), from, text);
    }
 
}


