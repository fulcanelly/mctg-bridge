package me.fulcanelly.tgbridge.tools.compact.message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.Data;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tools.compact.context.CompactionContext;
import me.fulcanelly.tgbridge.tools.compact.context.NoteMessageCtx;
import me.fulcanelly.tgbridge.tools.compact.visitor.CompactableVisitor;
import me.fulcanelly.tgbridge.utils.time.TimeoutManager;

@Data
public class NoteMessage implements CompactableMessage {

    static final long MAX_ALLOWED = 20;
    
    private List<String> notes = new ArrayList<>();

    private final TimeoutManager timeout = 
        new TimeoutManager(
            TimeUnit.SECONDS.toMillis(30)
        );

    long messageId;
    
    public NoteMessage(long sentMessage, String text) {
        this.messageId = sentMessage;
        this.notes.add(text);
    }

    private boolean isLimitExceeded() {
        return notes.size() > MAX_ALLOWED;
    }

    public boolean isExtendable() {
        return !(isLimitExceeded() || timeout.isTimeout());
    }

    public void append(String text) {
        timeout.update();
        notes.add(text);
    }  


    @Override
    public long getMessageID() {
        return messageId;
    }

    @Override
    public String getText() {
        return String.join("\n\n", notes);
    }

    @Override
    public void accept(CompactableVisitor visitor) {
        visitor.visit(this);
    }

}
