package me.fulcanelly.tgbridge.tools.compact.visitor;

import org.apache.commons.lang.NotImplementedException;

import me.fulcanelly.tgbridge.tools.compact.message.NoteMessage;
import me.fulcanelly.tgbridge.tools.compact.message.PlayerMessage;

public interface CompactableVisitor {

    default void visit(PlayerMessage msg) {
        throw new NotImplementedException();
    }

    default void visit(NoteMessage msg) {
        throw new NotImplementedException();
    }
    
}
