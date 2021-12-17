package me.fulcanelly.dither;

import java.io.File;
import java.io.IOException;

import me.fulcanelly.dither.handlers.BayerDitheringFacade;
import me.fulcanelly.dither.handlers.ErrorDiffusionFacade;
import me.fulcanelly.dither.handlers.ImageHandler;

import java.awt.Color;



public class Main {

    public static void main(String[] args) throws IOException {
        ImageHandler ih = null;
        if (args.length >= 0 ) {
            ih = new BayerDitheringFacade();
        } else {
            ih = new ErrorDiffusionFacade();
        }

        var innfolder = new File("imgs");
        innfolder.mkdir();
        new AllImagesProcessor(innfolder, ih).run();
    }
}

interface RangeLimiter {
   // int getLimitBy
}