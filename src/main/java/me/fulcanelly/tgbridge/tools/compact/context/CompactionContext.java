package me.fulcanelly.tgbridge.tools.compact.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.compact.message.CompactableMessage;

@Data @AllArgsConstructor
public abstract class CompactionContext {
    
    public TGBot bot;
    public Long chatID;

    public abstract CompactableMessage send();

    public void update(long msgID, String text ) {
        bot.editMessage(chatID, msgID, text);
    }

    public void autoUpdate(CompactableMessage msg) {
        bot.editMessage(chatID, msg.getMessageID(), msg.getText());
    }
    
}


