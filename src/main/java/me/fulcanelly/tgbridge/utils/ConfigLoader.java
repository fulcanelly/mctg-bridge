package me.fulcanelly.tgbridge.utils;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ConfigLoader {
    
    JSONObject data;
    final String fileName;
    
    public ConfigLoader(String fileName) {
        data = new JSONObject();
        this.fileName = fileName;
    }

    private boolean isDir(String path) {
        return new File(path).isDirectory();
    }

    private boolean isExist(String path) {
        return new File(path).exists();
    }

    private boolean checkDir(String path) {
        return isExist(path) && isDir(path);
    }

    public boolean load() {
        if(! checkDir("./plugins/tg-bridge")) {
            return false;
        }

        try {
            FileReader reader = new FileReader("./plugins/tg-bridge/" + fileName);
            Object data = new JSONParser().parse(reader); 
            this.data = (JSONObject)data;
        } catch (Throwable e){
            e.printStackTrace();
            //save();
        }
        return true;
    }

    public void save() {
        try{
            PrintWriter pw = new PrintWriter("./plugins/tg-bridge/" + fileName); 
            pw.write(this.data.toJSONString()); 
            pw.flush(); 
            pw.close();         
        } catch (Exception e) {
        }
    } 

    private <T>T get(String key) {
        return (T)data.get(key);
    }

    public String getApiToken() {
        return get("api_token");
    }

    public String getPinedChat() {
        return get("chat_id");
    }

    public void setApiToken(String apiToken) {
        data.put("api_token", apiToken);
    }

    public void setPinedChat(Long pinedChat) {
        data.put("chat_id", pinedChat);
    }
}