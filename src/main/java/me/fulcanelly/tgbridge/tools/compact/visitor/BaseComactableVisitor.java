package me.fulcanelly.tgbridge.tools.compact.visitor;

import me.fulcanelly.tgbridge.tools.compact.context.CompactionContext;

public abstract class BaseComactableVisitor implements CompactableVisitor {
    
    public abstract <T extends CompactionContext> T getCtx();
    public abstract boolean isCompacted();

}
