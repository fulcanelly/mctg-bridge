package me.fulcanelly.tgbridge.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lombok.SneakyThrows;

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

    static public String adjustSize(int len, String str) {
        if (str.length() > len) {
            return str.substring(0, len) + "...";
        }
        return str;
    }

    static public void delay(long time) {
        try { 
            Thread.sleep(time);
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
    
    public static String escapeMarkdown(String input) {
        return input
            .replace("_", "\\_")
            .replace("*", "\\*")
            .replace("[", "\\[")
            .replace("`", "\\`");

    }


    @SneakyThrows
    public static InputStream loadFileHTTPS(String url) {
        return new URL(url).openStream();
    }  

    @SneakyThrows
    public static String loadPage(String url) {
        var stream = new URL(url).openStream();
        return new String(stream.readAllBytes());
    }

} 