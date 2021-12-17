package me.fulcanelly.dither.handlers;

import java.awt.Color;

import me.fulcanelly.dither.algorithm.base.BayerDithering;
import me.fulcanelly.dither.utils.DotProvider;

public class BayerDitheringFacade implements ImageHandler {
    
    BayerDithering dither = new BayerDithering();

    @Override
    public int apply(int i, int j, DotProvider<Color> provider) {
        var r = dither.apply(provider.andThen(Color::getRed), i, j);
        var g = dither.apply(provider.andThen(Color::getGreen) , i, j);
        var b = dither.apply(provider.andThen(Color::getBlue), i, j);
       
        return new Color(r & 255, g & 255, b & 255).getRGB();
    }

}