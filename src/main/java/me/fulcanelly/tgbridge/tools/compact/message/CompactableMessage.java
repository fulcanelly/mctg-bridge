package me.fulcanelly.tgbridge.tools.compact.message;


import me.fulcanelly.tgbridge.tools.compact.visitor.CompactableVisitor;

public interface CompactableMessage {

    long getMessageID();
    String getText();
    void accept(CompactableVisitor visitor);
}
