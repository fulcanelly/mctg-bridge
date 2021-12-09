package me.fulcanelly.dither.utils;

public class PixelMapper {
    public ValMapper mapper;
    public int xShift, yShift;

    public double map(double val) {
        return mapper.map(val);
    }

    public PixelMapper(int xShift, int yShift, ValMapper mapper) {
        this.mapper = mapper;
        this.xShift = xShift;
        this.yShift = yShift;
    }

    public PixelMapper withXY(int x, int y) {
        return new PixelMapper(x, y, mapper);
    }

    public PixelMapper withMapper(ValMapper mapper) {
        return new PixelMapper(xShift, yShift, mapper);
    }
}