package me.fulcanelly.tgbridge.utils.container;

import java.util.function.Consumer;

public class VirtualConsumer<T> implements Consumer<T> {
        
    final Consumer<T> inner;

    public VirtualConsumer(Consumer<T> consumer) {
        inner = consumer;
    }

    @Override
    public void accept(T arg) {
        inner.accept(arg);
    }
}
