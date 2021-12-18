package me.fulcanelly.dither.algorithm;

import me.fulcanelly.dither.algorithm.base.GenericErrorDiffusion;
import me.fulcanelly.dither.utils.ErrorCalculator;
import me.fulcanelly.dither.utils.PixelMapper;

public class FloydSteinbergErrorDiffusion extends GenericErrorDiffusion {

    ErrorCalculator errorCalculator = new ErrorCalculator();

    @Override
    public PixelMapper[] getMappings() {
        /*
        return generateMappings(-16.0,
            new Double[][]{
                { null, Double.NaN, 7.0 },
                {  3.0, 5.0, 11.0 }
            }
        );*/

        return generateMappings(-48.0,
            new Double[][]{
                { null, null, Double.NaN, 7.0, 5.0 },
                { 3.0,   5.0,        7.0, 5.0, 3.0 },
                { 1.0,   3.0,        5.0, 3.0, 1.0 }
            }
        );
       /*
        return generateMappings(-2.0,
            new Double[][]{
                { Double.NaN, 1.0 },
                {  1.0, 0.0 }
            }
        ); */
    }



}