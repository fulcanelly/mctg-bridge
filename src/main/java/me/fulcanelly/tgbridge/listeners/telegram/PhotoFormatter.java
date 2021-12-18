package me.fulcanelly.tgbridge.listeners.telegram;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import io.raffi.drawille.Canvas;
import lombok.SneakyThrows;

import me.fulcanelly.dither.BrailleImageProcessor;
import me.fulcanelly.dither.handlers.BayerDitheringFacade;

public class PhotoFormatter {
    final int max_allowed;
    
    public PhotoFormatter(int max) {
        this.max_allowed = max; 
    }

    private BufferedImage scaleToFitInChat(BufferedImage photo) {
        var maxSide = Math.max(photo.getWidth(), photo.getHeight());
        var ratio = max_allowed / (double)maxSide;
        //to fit in limit
        int width = (int)(photo.getWidth() * ratio), 
            height = (int)(photo.getHeight() * ratio);

        //to fork with Braille font
        width -= width % 2;
        height -= height % 4;

        //from https://stackoverflow.com/questions/4216123/how-to-scale-a-bufferedimage

        BufferedImage resized = new BufferedImage(width, height, photo.getType());
        Graphics2D graph = resized.createGraphics();
        graph.scale(width / (double) photo.getWidth(), height / (double) photo.getHeight());
        graph.drawImage(resized, 0, 0, null);
        graph.dispose();

        BufferedImage before = photo;
        int w = before.getWidth();
        int h = before.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(width / (double)photo.getWidth(), height / (double) photo.getHeight());
        AffineTransformOp scaleOp = 
        new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(before, after);

        return after.getSubimage(0, 0, width, height); //to insure no problems will appear
    }

    @SneakyThrows
    public Canvas imageToBraille(BufferedImage image) {
        var scaled = scaleToFitInChat(image);
        return new BrailleImageProcessor(
            scaled, new BayerDitheringFacade()//new ErrorDiffusionFacade()
        ).process();
    }


}