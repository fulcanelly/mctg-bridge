package me.fulcanelly.dither.utils;

public class Linear2DArray {
    
    int[] array;
    public int x;
    public int y;

    public Linear2DArray(int x, int y) {
        this.x = x;
        this.y = y;
        array = new int[x * y];
    }

    public int getAt(int i, int j) {
        return array[j * x + i];
    }

    public void setAt(int i, int j, int v) {
        array[j * x + i] = v;
    }
}