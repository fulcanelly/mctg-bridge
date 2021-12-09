package me.fulcanelly.dither.algorithm.base;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import me.fulcanelly.dither.utils.DotProvider;
import me.fulcanelly.dither.utils.ErrorCalculator;
import me.fulcanelly.dither.utils.Linear2DArray;
import me.fulcanelly.dither.utils.PixelMapper;
import me.fulcanelly.dither.utils.ValMapper;

public abstract class GenericErrorDiffusion {
    
    ErrorCalculator errorCalculator = new ErrorCalculator();

    public abstract PixelMapper[] getMappings();

    PixelMapper[] mappings = getMappings();

    public ValMapper makeMapper(double mult) {
        return x -> (int)(x * mult);
    }

    public Function<PixelMapper, PixelMapper> makePimapMapper(int x, int y) {
        return pimap -> pimap.withXY(pimap.xShift - x, pimap.yShift - y);
    }

    public PixelMapper[] generateMappings(double devisor, Double [][] array) {
        List<PixelMapper> result = new LinkedList<>();
        int x = 0, y = 0;

        for (int i = 0; i < array.length; i ++) {
            for (int j = 0; j < array[i].length; j++) {
                var value = array[i][j];
                if (value == null) {
                    continue;
                }
                if (Double.isNaN(value)) {
                    x = i;
                    y = j;
                }
                result.add(new PixelMapper(i, j, makeMapper(value)));
            }
        }
        return result.stream()
            .map(makePimapMapper(x, y).andThen(it -> it.withMapper(v -> it.mapper.map(v) / devisor)))
            .toArray(PixelMapper[]::new);
    }

    public void setByCheckedBounds(Linear2DArray errors, int x, int y, int it) {
        if (x < 0 || x >= errors.x) {
            return;
        }
        if (y < 0 || y >= errors.y) {
            return;
        }
        //errors[x][y] = it;
        errors.setAt(x, y, errors.getAt(x, y) + it);
        
    }

    public int truncate(int val) {
        if (val < 0) {
            return 0;
        }

        if (val > 255) {
            return 255;
        }
        
        return val;
    }

    public int apply(DotProvider<Integer> provider, Linear2DArray errors, int x, int y) {
        var dot = truncate(provider.getDotAt(x, y) + errors.getAt(x, y));
        var closest = errorCalculator.getClosest(dot);
        var error = errorCalculator.getError(closest, dot);
        for (var mapping : mappings) {
            setByCheckedBounds(errors, x + mapping.xShift, y + mapping.yShift, (int)mapping.map(error));
         //   errors[x + mapping.xShift][y + mapping.yShift] += mapping.map(error);
        }
        return closest;
    }

}