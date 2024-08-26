package me.fulcanelly.tgbridge.tools.compact.visitor;

import me.fulcanelly.tgbridge.tools.compact.message.NoteMessage;
import me.fulcanelly.tgbridge.tools.compact.message.PlayerMessage;

public interface CompactableVisitor {

    default void visit(PlayerMessage msg) {
    }

    default void visit(NoteMessage msg) {
    }
    
}
