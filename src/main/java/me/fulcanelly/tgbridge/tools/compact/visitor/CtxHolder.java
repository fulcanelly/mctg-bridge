package me.fulcanelly.tgbridge.tools.compact.visitor;

import me.fulcanelly.tgbridge.tools.compact.context.CompactionContext;

public interface CtxHolder {
    <T extends CompactionContext> T getCtx();
}
