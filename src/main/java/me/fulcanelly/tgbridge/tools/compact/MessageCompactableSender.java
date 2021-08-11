package me.fulcanelly.tgbridge.tools.compact;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.compact.context.NoteMessageCtx;
import me.fulcanelly.tgbridge.tools.compact.context.SignedMessageCtx;
import me.fulcanelly.tgbridge.tools.compact.message.CompactableMessage;
import me.fulcanelly.tgbridge.tools.compact.visitor.CompactableVisitor;
import me.fulcanelly.tgbridge.tools.compact.visitor.Compacted;
import me.fulcanelly.tgbridge.tools.compact.visitor.CtxHolder;
import me.fulcanelly.tgbridge.tools.compact.visitor.NoteMessageCompactorVisitor;
import me.fulcanelly.tgbridge.tools.compact.visitor.PlayerMessageCompactorVisitor;

@RequiredArgsConstructor @Data
public class MessageCompactableSender {

    final TGBot bot;
    final Long chatId;

    private final AtomicLong actualLast = new AtomicLong(-1);
    public Optional<CompactableMessage> lastSent = Optional.empty();

    public boolean setActualLast(long last) {
        actualLast.set(last);
        return true;
    }

    private boolean setAllLastOfCompactable(CompactableMessage cmsg) {
        lastSent = Optional.of(cmsg);
        return setActualLast(cmsg.getMessageID());
    } 
    
    <V extends CompactableVisitor & Compacted> 
    boolean compactUsingCtxAndCompactor(V compactor) {
        return lastSent.map(cpt -> {
                cpt.accept(compactor);
                return compactor.isCompacted();
            })
            .orElse(false);
    }
 
    <V extends CompactableVisitor & Compacted & CtxHolder> 
    boolean tryCompactOrSendNew(V compactor) {
        return Objects.isNull(chatId) ||
            compactUsingCtxAndCompactor(compactor) || 
            setAllLastOfCompactable(compactor.getCtx().send());
    }

    public void sendAsPlayer(String from, String text) {
        tryCompactOrSendNew(
            new PlayerMessageCompactorVisitor(
                actualLast, new SignedMessageCtx(bot, chatId, from, text))
        );
    } 

    public void sendNote(String text) {
        tryCompactOrSendNew(
            new NoteMessageCompactorVisitor(
                actualLast, new NoteMessageCtx(bot, chatId, text))
        );
    }
}
