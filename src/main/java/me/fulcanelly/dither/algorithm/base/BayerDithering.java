package me.fulcanelly.dither.algorithm.base;

import me.fulcanelly.dither.utils.DotProvider;

public class BayerDithering {

    double[][] mappings = multBy(new double[][]{
        { 0,  8,  2, 10 },
        { 12, 4, 14,  6 },
        { 3, 11,  1,  9 },
        { 15, 7, 13,  5 }
    });


    int dX = 4, dY = 4;
/*
    double[][] mappings = multBy(new double[][]{
        { 0, 2},
        { 3, 1 }
    });

    int dX = 2, dY = 2;
*/

    static double[][] multBy(double[][] it) {
       // var v = 256 / 16.0;
        var v = 256 / 4.0;
        for (int i = 0; i < it.length; i++) {
            for (int j = 0; j < it[i].length; j++) {
                it[i][j] *= v;
            }
        }
        return it;
    }
    
    public int apply(DotProvider<Integer> provider, int i, int j) {
        var ci = i % dX;
        var cj = j % dY;
        if (provider.getDotAt(i, j) > mappings[ci][cj]) {
            return 255;
        } else {
            return 0;
        }
    }
}