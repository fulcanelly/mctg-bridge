package me.fulcanelly.dither;

import java.awt.Color;
import java.awt.image.BufferedImage;

import io.raffi.drawille.Canvas;
import lombok.AllArgsConstructor;
import me.fulcanelly.dither.handlers.ImageHandler;
import me.fulcanelly.dither.utils.DotProvider;

@AllArgsConstructor
public class BrailleImageProcessor {
    
    BufferedImage input;

    ImageHandler handler;
   
    Color getAvgColor(int color) {
        var avg = findAvg(new Color(color));
        return new Color(avg & 255, avg & 255, avg & 255);
    }

    int findAvg(Color color) {
        return (color.getGreen() + color.getRed() + color.getAlpha()) / 3;
    }
    public Canvas process() {
        Canvas canvas = new Canvas(input.getWidth()/ 2, input.getHeight() / 4); 
        handler.setup(input.getWidth(), input.getHeight());

        DotProvider<Color> provider = (x, y) -> new Color(input.getRGB(x, y));

        for (int i = 0; i < input.getWidth(); i++) {
            for (int j = 0; j < input.getHeight(); j++) {
               // System.out.println(new Color(handler.apply(i, j, provider)).getGreen());
                canvas.change(i, j, 128 < new Color(handler.apply(i, j, provider)).getGreen());
                if (i == input.getWidth() - 1 && j == input.getHeight() - 1) {
                    canvas.change(i, j, true);
                }
            }
        }

        return canvas;
    }
} 