package me.fulcanelly.tgbridge.listeners.telegram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import io.raffi.drawille.Canvas;
import lombok.SneakyThrows;

import me.fulcanelly.dither.BrailleImageProcessor;
import me.fulcanelly.dither.handlers.BayerDitheringFacade;
import net.md_5.bungee.api.ChatColor;

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
        var ratio = max_allowed / (double) maxSide;
        // to fit in limit
        int width = (int) (photo.getWidth() * ratio),
                height = (int) (photo.getHeight() * ratio);

        return resizeImage(photo, width - width % 2, height - height % 4); // to insure no problems will appear
    }

    @SneakyThrows
    public Canvas imageToBraille(BufferedImage image) {
        var scaled = scaleToFitInChat(image);
        return new BrailleImageProcessor(
                scaled, new BayerDitheringFacade()// new ErrorDiffusionFacade()
        ).process();
    }

    @EventHandler
    void x(ProjectileHitEvent event) {
        // event./
    }

    @EventHandler
    void ok(ChunkLoadEvent event) {
        event.getChunk().getEntities();
    }

    @SneakyThrows
    public String imageToCollorTextBrille(BufferedImage image) {
        var scaled = scaleToFitInChat(image);
        var text = new BrailleImageProcessor(scaled, new BayerDitheringFacade())
                .process()
                .render(new ByteArrayOutputStream())
                .toString();

        var chars = text.toCharArray();

        var colors = new ArrayList<>();

        for (int i = 0; i < scaled.getWidth(); i += 2) {
            for (int j = 0; j < scaled.getHeight(); j += 4) {
                var color = ColorMapperHSV.mapColor(getMostCommonCollorFor(scaled, i, j));
                colors.add(color);
            }
        }

        var builder = new StringBuilder();

        int counter = 0;

        for (char symbol : chars) {
            if (symbol == '\n') {
                builder.append(symbol);
                continue;
            }

            builder.append(colors.get(counter));
            builder.append(symbol);

            counter++;
        }

        return builder.toString();
    }

    Color getMostCommonCollorFor(BufferedImage scaled, int x, int y) {
        var colorCounts = new HashMap<Color, Integer>();

        for (int i = x; i < Math.min(x + 2, scaled.getWidth()); i++) {
            for (int j = y; j < Math.min(y + 4, scaled.getHeight()); j++) {
                var target = new Color(scaled.getRGB(i, j));

                colorCounts.merge(target, 1, Integer::sum);

                // var count = map.get(target);
                // if (count == null) {
                // map.put(target, 1);
                // } else {
                // map.put(target, count + 1);
                // }
            }
        }

        return colorCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .findFirst().get()
                .getKey();
    }

    // void ok() {

    // }
}
