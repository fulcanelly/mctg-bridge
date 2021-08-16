package me.fulcanelly.tgbridge.tools.compact.message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tools.compact.context.CompactionContext;
import me.fulcanelly.tgbridge.tools.compact.context.SignedMessageCtx;
import me.fulcanelly.tgbridge.tools.compact.visitor.CompactableVisitor;
import me.fulcanelly.tgbridge.utils.time.TimeoutManager;


@ToString
public class PlayerMessage implements CompactableMessage {

    final String name;
    final List<String> lines = new ArrayList<>();
    final long message_id;
    
    
    final static long MAX_SIZE = 20;

    TimeoutManager timeout = new TimeoutManager(MAX_TIMEOUT_MILLIS);

    final static long MAX_TIMEOUT_MILLIS = 30 * 1000;

    public PlayerMessage(long msg_id, @NonNull String from, @NonNull String text) {
        this.message_id = msg_id;
        this.name = from;
        this.lines.add(text);
    }   

    public void mergeWith(String new_text) {
        timeout.update();
        lines.add(new_text);
    }

    private boolean isFrom(String another_name) {
        if (another_name == null) {
            return false;
        }
        return name.equals(another_name);
    }   

    private boolean isLimitExceeded() {
        return lines.size() > MAX_SIZE;
    }

    public boolean isMergeableWith(String from) {
                    
        if (isLimitExceeded() || timeout.isTimeout()) {
            return false;
        } 

        return isFrom(from);
    }

    @Override
    public long getMessageID() {
        return message_id;
    }

    @Override
    public String getText() {
        StringJoiner joiner = new StringJoiner("\n\n");
        
        lines.forEach(joiner::add);
        return String.format("*<%s>*\n", name) + joiner.toString();
    }

    @Override
    public void accept(CompactableVisitor visitor) {
        visitor.visit(this);
    }

}
