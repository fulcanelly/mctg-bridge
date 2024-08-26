package me.fulcanelly.tgbridge.tools.compact.context;

import lombok.Getter;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.compact.message.CompactableMessage;
import me.fulcanelly.tgbridge.tools.compact.message.NoteMessage;


@Getter
public class NoteMessageCtx extends CompactionContext {

    final String text;

    public NoteMessageCtx(TGBot bot, Long chatID, String text) {
        super(bot, chatID);
        this.text = text;
    }


    @Override
    public CompactableMessage send() {
        return new NoteMessage(
            bot.sendMessage(chatID, text).getMsgId(), text
        );
    }


}
