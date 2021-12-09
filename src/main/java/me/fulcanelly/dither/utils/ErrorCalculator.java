package me.fulcanelly.dither.utils;

public class ErrorCalculator {
    int maxValue = 255;

    public int getError(int chosen, int prev) {
        return chosen - prev;
    }

    public int getClosest(int value) {
        if (value >= 127)
            return 255;
        else 
            return 0;
    }
}