package me.fulcanelly.tgbridge.utils;

import java.util.Map;

import org.json.simple.JSONObject; 
import org.json.simple.parser.*; 

import java.net.URL;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;


public class UsefulStuff {
    static public JSONObject stringToJSON(String text) {
        try {
            return (JSONObject)new JSONParser().parse(text);
        } catch (ParseException e ) {}

        return new JSONObject();
    }

    public static String getEnv(String name) {
        Map<String, String> env = System.getenv();
        return env.get(name);
    }

    public static String loadPage(String url) {
        String pageText = new String();
        try{
            InputStream inp = new URL(url)
                .openConnection()
                .getInputStream();

            byte[] buffer = new byte[4096];
            ByteArrayOutputStream page = new ByteArrayOutputStream();

            int length = 0;
            while ((length = inp.read(buffer)) != -1) {
                page.write(buffer, 0, length);
            }
            pageText = page.toString();
        } catch(IOException e) {}
        return pageText;
    } 

} 