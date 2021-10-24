package me.fulcanelly.tgbridge.tapi;

import org.json.simple.JSONObject;

public class From  {
    public JSONObject from = new JSONObject();

    public From(JSONObject from) {
        this.from = from;
    }    

    public From(Object from) {
        this.from = (JSONObject)from;
    }

    public String getUsername() {
        return (String)from.get("username");
    }

    public String getName() {
        return (String)from.get("first_name");
    }
    
    public Long getId() {
        return (Long)from.get("id");
    }

    boolean isBot() {
        Object is_bot = from.get("is_bot");

        if(is_bot != null) {
            return (boolean)is_bot;
        } 
        
        return false;
    }

    boolean isPrivate() {
        Object type = from.get("type");
        if(type != null) {
            return type.equals("private");
        } 

        return false;       
    }
}