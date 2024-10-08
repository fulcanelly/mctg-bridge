package me.fulcanelly.tgbridge.tools.compact.visitor;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.fulcanelly.tgbridge.tools.compact.context.NoteMessageCtx;
import me.fulcanelly.tgbridge.tools.compact.message.NoteMessage;

@RequiredArgsConstructor @Getter
public class NoteMessageCompactorVisitor extends BaseComactableVisitor {

    @NonNull AtomicLong actualLastMsgId;
    @NonNull NoteMessageCtx ctx;

    boolean compacted = false;
    
    @Override
    public void visit(NoteMessage msg) {
        var messageId = msg.getMessageID();
        if (messageId == actualLastMsgId.get() && msg.isExtendable()) {
            msg.append(ctx.getText());
            ctx.autoUpdate(msg);
            compacted = true;
        }
    }
    
    @Override
    public boolean isCompacted() {
        return compacted;
    }
}
