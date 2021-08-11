package me.fulcanelly.tgbridge.tools.compact.visitor;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.fulcanelly.tgbridge.tools.compact.context.SignedMessageCtx;
import me.fulcanelly.tgbridge.tools.compact.message.PlayerMessage;

@RequiredArgsConstructor @Getter
public class PlayerMessageCompactorVisitor implements CompactableVisitor, Compacted, CtxHolder {
    
    @NonNull AtomicLong actualLastMsgId;

    @NonNull SignedMessageCtx ctx;
    
    boolean compacted = false;

    @Override
    public void visit(PlayerMessage msg) {
        var msgId = msg.getMessageID();
        if (msgId == actualLastMsgId.get() && msg.isMergeableWith(ctx.getFrom())) {
            msg.mergeWith(ctx.getText());
            ctx.autoUpdate(msg);
            compacted = true;
        }
    }

    @Override
    public boolean isCompacted() {
        return compacted;
    }
}
