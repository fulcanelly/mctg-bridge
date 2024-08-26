package me.fulcanelly.tgbridge.listeners.telegram;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import io.raffi.drawille.Canvas;
import lombok.SneakyThrows;

import me.fulcanelly.dither.BrailleImageProcessor;
import me.fulcanelly.dither.handlers.BayerDitheringFacade;

public class PhotoFormatter {
    final int max_allowed;
    
    public PhotoFormatter(int max) {
        this.max_allowed = max; 
    }

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private BufferedImage scaleToFitInChat(BufferedImage photo) {
        var maxSide = Math.max(photo.getWidth(), photo.getHeight());
        var ratio = max_allowed / (double)maxSide;
        //to fit in limit
        int width = (int)(photo.getWidth() * ratio), 
            height = (int)(photo.getHeight() * ratio);

        return resizeImage(photo, width - width % 2, height - height % 4); //to insure no problems will appear
    }

    @SneakyThrows
    public Canvas imageToBraille(BufferedImage image) {
        var scaled = scaleToFitInChat(image);
        return new BrailleImageProcessor(
            scaled, new BayerDitheringFacade()//new ErrorDiffusionFacade()
        ).process();
    }


}