package me.fulcanelly.dither;

import java.awt.Color;
import java.awt.image.BufferedImage;

import me.fulcanelly.dither.handlers.ImageHandler;
import me.fulcanelly.dither.utils.DotProvider;

public class BufferedImageProcessor {
    
    BufferedImage input;
    ImageHandler handler;

    public BufferedImageProcessor(BufferedImage input, ImageHandler handler) {
        this.input = input;
        this.handler = handler;
    }

    public BufferedImage process() {
        DotProvider<Color> provider = (x, y) -> new Color(input.getRGB(x, y));

        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
        handler.setup(input.getWidth(), input.getHeight());

        System.out.println("mapping pixels");

        for (int i = 0; i < input.getWidth(); i++) {
            for (int j = 0; j < input.getHeight(); j++) {
                result.setRGB(i, j, handler.apply(i, j, provider));
            }
        }

        return result;
    }
}
