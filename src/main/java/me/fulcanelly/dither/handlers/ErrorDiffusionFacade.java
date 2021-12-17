package me.fulcanelly.dither.handlers;

import java.awt.Color;
import java.util.stream.Stream;

import me.fulcanelly.dither.algorithm.FloydSteinbergErrorDiffusion;
import me.fulcanelly.dither.utils.DotProvider;
import me.fulcanelly.dither.utils.Linear2DArray;

public class ErrorDiffusionFacade implements ImageHandler {

    FloydSteinbergErrorDiffusion applicator = new FloydSteinbergErrorDiffusion();


    Linear2DArray errorsR, errorsG, errorsB;

    static int[][] makeArray(int x, int y) {
        return Stream.generate(() -> new int[y])
            .limit(x).toArray(int[][]::new);
    }

    @Override
    public int apply(int i, int j, DotProvider<Color> provider) {
        var r = applicator.apply(provider.andThen(Color::getRed), errorsR, i, j);
        var g = applicator.apply(provider.andThen(Color::getGreen), errorsG, i, j);
        var b = applicator.apply(provider.andThen(Color::getBlue), errorsB, i, j);
       
        return new Color(r & 255, g & 255, b & 255).getRGB();
    }


    @Override
    public void setup(int maxX, int maxY) {
        errorsR = new Linear2DArray(maxX, maxY);
        errorsG = new Linear2DArray(maxX, maxY);
        errorsB = new Linear2DArray(maxX, maxY);
    }
    
}