package me.fulcanelly.tgbridge.tools.hooks;

public interface ForeignPluginHook {
    boolean isAvailable();
    void setup();
}
