package me.fulcanelly.tgbridge.listeners.telegram;

import org.bukkit.ChatColor;
import java.awt.Color;

public class ColorMapperHSV {

    private static final Color[] colors = {
        new Color(0x000000), // Black
        new Color(0x0000AA), // Dark Blue
        new Color(0x00AA00), // Dark Green
        new Color(0x00AAAA), // Dark Aqua
        new Color(0xAA0000), // Dark Red
        new Color(0xAA00AA), // Dark Purple
        new Color(0xFFAA00), // Gold
        new Color(0xAAAAAA), // Gray
        new Color(0x555555), // Dark Gray
        new Color(0x5555FF), // Blue
        new Color(0x55FF55), // Green
        new Color(0x55FFFF), // Aqua
        new Color(0xFF5555), // Red
        new Color(0xFF55FF), // Light Purple
        new Color(0xFFFF55), // Yellow
        new Color(0xFFFFFF)  // White
    };

    private static final ChatColor[] chatColors = {
        ChatColor.BLACK,
        ChatColor.DARK_BLUE,
        ChatColor.DARK_GREEN,
        ChatColor.DARK_AQUA,
        ChatColor.DARK_RED,
        ChatColor.DARK_PURPLE,
        ChatColor.GOLD,
        ChatColor.GRAY,
        ChatColor.DARK_GRAY,
        ChatColor.BLUE,
        ChatColor.GREEN,
        ChatColor.AQUA,
        ChatColor.RED,
        ChatColor.LIGHT_PURPLE,
        ChatColor.YELLOW,
        ChatColor.WHITE
    };

    private static float[] rgbToHsv(Color color) {
        float[] hsv = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
        return hsv;
    }

    private static double hsvDistance(float[] hsv1, float[] hsv2) {
        // Simple difference calculation; can be adjusted for more accurate color perception models
        double dh = Math.min(Math.abs(hsv1[0] - hsv2[0]), 360 - Math.abs(hsv1[0] - hsv2[0])) / 180.0;
        double ds = Math.abs(hsv1[1] - hsv2[1]);
        double dv = Math.abs(hsv1[2] - hsv2[2]) / 255.0;
        return Math.sqrt(dh * dh + ds * ds + dv * dv);
    }

    public static ChatColor mapColor(Color color) {
        float[] targetHsv = rgbToHsv(color);
        ChatColor closestMatch = ChatColor.WHITE;
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < colors.length; i++) {
            float[] hsv = rgbToHsv(colors[i]);
            double distance = hsvDistance(targetHsv, hsv);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestMatch = chatColors[i];
            }
        }
        return closestMatch;
    }
}
