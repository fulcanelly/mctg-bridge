package me.fulcanelly.tgbridge.tools.compact;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;



import lombok.Data;
import lombok.SneakyThrows;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.compact.context.NoteMessageCtx;
import me.fulcanelly.tgbridge.tools.compact.context.SignedMessageCtx;
import me.fulcanelly.tgbridge.tools.compact.message.CompactableMessage;
import me.fulcanelly.tgbridge.tools.compact.visitor.BaseComactableVisitor;
import me.fulcanelly.tgbridge.tools.compact.visitor.CompactableVisitor;
import me.fulcanelly.tgbridge.tools.compact.visitor.Compacted;
import me.fulcanelly.tgbridge.tools.compact.visitor.NoteMessageCompactorVisitor;
import me.fulcanelly.tgbridge.tools.compact.visitor.PlayerMessageCompactorVisitor;

@Data
public class MessageCompactableSender {

    final TGBot bot;
    final Long chatId;

    ArrayBlockingQueue<BaseComactableVisitor> quque = new ArrayBlockingQueue<>(10);

    public MessageCompactableSender(TGBot bot, Long chatId) {
        this.bot = bot;
        this.chatId = chatId;

  
        new Thread(() -> {
            for (;;) {
                try {
                    var comapctor = quque.take();
                    tryCompactOrSendNew(comapctor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        
    }

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
    boolean compactUsingCtxAndCompactor(BaseComactableVisitor compactor) {
        return lastSent.map(cpt -> {
                cpt.accept(compactor);
                return compactor.isCompacted();
            })
            .orElse(false);
    }
 
    boolean tryCompactOrSendNew(BaseComactableVisitor compactor) {
        return Objects.isNull(chatId) ||
            compactUsingCtxAndCompactor(compactor) || 
            setAllLastOfCompactable(compactor.getCtx().send());
    }

    @SneakyThrows
    public void sendAsPlayer(String from, String text) {
        quque.add(
            new PlayerMessageCompactorVisitor(
                actualLast, new SignedMessageCtx(bot, chatId, from, text))
        );
    } 

    @SneakyThrows
    public void sendNote(String text) {
        quque.add(
            new NoteMessageCompactorVisitor(
                actualLast, new NoteMessageCtx(bot, chatId, text))
        );
    }
}
