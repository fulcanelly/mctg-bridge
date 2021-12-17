package me.fulcanelly.dither.handlers;

import java.awt.Color;

import me.fulcanelly.dither.utils.DotProvider;

public interface ImageHandler {

    default void setup(int maxX, int maxY) {};
    int apply(int i, int j, DotProvider<Color> provider);

}