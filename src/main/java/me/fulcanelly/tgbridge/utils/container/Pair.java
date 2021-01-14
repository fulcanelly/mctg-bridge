package me.fulcanelly.tgbridge.utils.container;

import java.util.function.Supplier;

public class Pair<T, K> {
    public T first;
    public K second;

    public Pair(T first, K second) {
        this.first = first;
        this.second = second;
    }

    public Pair(Supplier<T> first, Supplier<K> second) {
        this.first = first.get();
        this.second = second.get();        
    }
}