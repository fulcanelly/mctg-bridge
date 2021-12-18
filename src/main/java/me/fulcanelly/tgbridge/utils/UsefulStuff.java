package me.fulcanelly.tgbridge.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


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


    @SneakyThrows
    public static InputStream loadFileHTTPS(String url) {
        return new URL(url).openStream();
    }  

    @SneakyThrows
    public static String loadPage(String url) {
        String pageText = new String();
        URLConnection urlConnection = new URL(url).openConnection();

        if (urlConnection instanceof HttpURLConnection) {
            var responseCode = ((HttpURLConnection)urlConnection).getResponseCode();
            switch (responseCode) {
                case HttpURLConnection.HTTP_NOT_FOUND:
                    throw new RuntimeException("Not found");
            }
        }

        InputStream inp = urlConnection.getInputStream();
        
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream page = new ByteArrayOutputStream();

        int length;
        while ((length = inp.read(buffer)) != -1) {
            page.write(buffer, 0, length);
        }
        pageText = page.toString();


        return pageText;
    }

} 