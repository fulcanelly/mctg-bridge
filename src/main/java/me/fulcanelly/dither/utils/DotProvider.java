package me.fulcanelly.dither.utils;

import java.util.function.Function;

public interface DotProvider<T> {
    T getDotAt(int x, int y);

    default <K> DotProvider<K> andThen(Function<T, K> mapper) {
        return (x, y) -> mapper.apply(getDotAt(x, y));
    }
}