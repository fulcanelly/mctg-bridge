package me.fulcanelly.tgbridge.tools.compact.message;

import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import lombok.Data;
import me.fulcanelly.tgbridge.tools.compact.visitor.CompactableVisitor;
import me.fulcanelly.tgbridge.utils.data.DuplicateLessList;
import me.fulcanelly.tgbridge.utils.time.TimeoutManager;

@Data
public class NoteMessage implements CompactableMessage {

    static final long MAX_ALLOWED = 20;
    
    private DuplicateLessList<String> notes = new DuplicateLessList<>();

    private final TimeoutManager timeout = 
        new TimeoutManager(
            TimeUnit.MINUTES.toMillis(30)
        );

    long messageId;
    
    public NoteMessage(long sentMessage, String text) {
        this.messageId = sentMessage;
        this.notes.add(text);
    }

    private boolean isLimitExceeded() {
        return notes.getList().size() > MAX_ALLOWED;
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
        var joiner = new StringJoiner("\n");
        notes.removeDuplicatesByMaxStep();
        var list = notes.getList();

        int countLast = -1;

        for (var freqvalue : list) {
            var count = freqvalue.getCount();
            
            if (countLast != count) {
                joiner.add(count == 1 ? "\n" : "\n # repeats " + count + " times \n");
            }   

            joiner.add(freqvalue.getValue());
            countLast = freqvalue.getCount();
        }

        return joiner.toString(); //String.join("\n\n", notes);
    }

    @Override
    public void accept(CompactableVisitor visitor) {
        visitor.visit(this);
    }

}
