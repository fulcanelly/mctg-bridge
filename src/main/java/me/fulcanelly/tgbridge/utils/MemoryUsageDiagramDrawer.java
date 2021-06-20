package me.fulcanelly.tgbridge.utils;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.raffi.drawille.Canvas;
import lombok.SneakyThrows;

public class MemoryUsageDiagramDrawer {

    public MemoryUsageDiagramDrawer(int width, int height) {
        if (width % 2 != 0 || height % 3 != 0) {
            throw new RuntimeException("width should be devideable by 2 and height by 3");
        }
        this.width = width;
        this.height = height;
        canvas = new Canvas(width / 2, height / 3);
    }

    final int width, height;
    final Canvas canvas;
    final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    final Runtime runtime = Runtime.getRuntime();
    final static long MB = 1024 * 1024;

    List<Integer> values = new LinkedList<>();
    
    public void start() {
        service.scheduleAtFixedRate(() -> {
            try {
                this.updateValues();
            } catch (Throwable e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }, 0, 300, TimeUnit.MILLISECONDS);
    }

    static int mapValue(long value, long maxOld, long maxNew) {
        float km = (float)maxNew / (float)maxOld;
        return (int) (km * value);
    }

    long getMaxMemory() {
        return runtime.totalMemory() / MB;
    }

    long getUsedMemory() {
        long totalMemory = getMaxMemory();
        long freeMemory = runtime.freeMemory() / MB;
        return totalMemory - freeMemory;
    }

    synchronized void updateValues() {
        values.add(
            height - mapValue(getUsedMemory(), getMaxMemory(), height)
        );

        if (values.size() >= width) {
            values = values.subList(1, values.size());
        }
    }

    //taken from https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Java
    static void drawLine(Canvas c, int fromX, int fromY, int toX, int toY) {
        int d = 0;
 
        int dx = Math.abs(fromX - toX);
        int dy = Math.abs(fromY - toY);
 
        int dx2 = 2 * dx; // slope scaling factors to
        int dy2 = 2 * dy; // avoid floating point
 
        int ix = fromX < toX ? 1 : -1; // increment direction
        int iy = fromY < toY ? 1 : -1;
 
        int x = fromX;
        int y = fromY;
 
        if (dx >= dy) {
            while (x != toX) {
                c.change(x, y, true);
             
                x += ix;
                d += dy2;
                if (d > dx) {
                    y += iy;
                    d -= dx2;
                }
            }
        } else {
            while (y != toY) {
                c.change(x, y, true);
     
                y += iy;
                d += dx2;
                if (d > dy) {
                    x += ix;
                    d -= dy2;
                }
            }
        }
    }

    @SneakyThrows
    synchronized public String draw() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        canvas.clear();
        for (int i = 1; i < values.size(); i++) {
            drawLine(canvas, i - 1, values.get(i - 1), i, values.get(i));
          //  System.out.printf("x y: %d %d\n", i, 36 - values.get(i));
          //  canvas.change(i, values.get(i), true);
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //System.out.printf("x y: %d %d\n", i, j);

                if (i % 4 == 0 && j % 4 == 0) {
                    canvas.change(i, j, true);
                }        
            }   
        }
        return canvas.render(baos).toString();
    }

    public String getMemory() {
        final long mb = 1024 * 1024;
        Runtime rtime = Runtime.getRuntime();
        
        long totalMemory = rtime.totalMemory() / mb;
        long freeMemory = rtime.freeMemory() / mb;
        long usedMemory = totalMemory - freeMemory;

        return String.format("Memory usage: %d MB / %d MB ", usedMemory, totalMemory);
    }

    public String toString() {
        return this.draw() + '\n' + 
            getMemory();
    }

}

