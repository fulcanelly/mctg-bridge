package me.fulcanelly.dither.utils;

public interface IntDotProvider extends DotProvider<Integer> {
    Integer getDotAt(int x, int y);
}