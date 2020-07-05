package me.fulcanelly.tgbridge.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class UsefulStuff {
    static public JSONObject stringToJSON(String text) {
        try {
            return (JSONObject) new JSONParser().parse(text);
        } catch (ParseException ignored) {

        }

        return new JSONObject();
    }

    static public void delay(long time) {
        try { 
            Thread.sleep(time);
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
    public static String formatMarkdown(String input) {
        return input
            .replace("_", "\\_")
            .replace("*", "\\*")
            .replace("[", "\\[")
            .replace("`", "\\`");

    }

    public static String formatHtml(String input) {
        return input
            .replace(">", "&gt;")
            .replace("<", "&lt;")
            .replace("&", "&amp;");
    }

    public static String loadPage(String url) {
        String pageText = "";

        try {
            InputStream inp = new URL(url)
                .openConnection()
                .getInputStream();

            byte[] buffer = new byte[4096];
            ByteArrayOutputStream page = new ByteArrayOutputStream();

            int length;
            while ((length = inp.read(buffer)) != -1) {
                page.write(buffer, 0, length);
            }
            pageText = page.toString();
        } catch (IOException ignored) {

        }

        return pageText;
    }

} 