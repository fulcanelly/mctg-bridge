package me.fulcanelly.dither;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import me.fulcanelly.dither.handlers.ImageHandler;
import me.fulcanelly.dither.utils.Traverser;


public class AllImagesProcessor {
    Traverser traverser; 
    ImageHandler algorithm;

    AllImagesProcessor(File file, ImageHandler algorithm) {
        traverser = new Traverser(file);
    }

    public void apply(File path) {

        System.out.println("handling " + path);
        var iproc = new BufferedImageProcessor(load(path), algorithm);
        System.out.println("saving");
        var out = new File("out");
        out.mkdir();
        store(iproc.process(), new File(out, path.getName()));
    }

    
    public BufferedImage load(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void store(BufferedImage img, File path) {
        try {
            ImageIO.write(img, "png", new File(path.toString() +  ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        traverser.traverse(this::apply);
    }
}
