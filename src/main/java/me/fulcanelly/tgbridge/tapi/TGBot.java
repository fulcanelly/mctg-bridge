package me.fulcanelly.tgbridge.tapi;

import java.util.HashMap;
import java.util.Map;

import me.fulcanelly.tgbridge.TgBridge;
import me.fulcanelly.tgbridge.utils.UsefulStuff;
import me.fulcanelly.tgbridge.utils.events.detector.EventDetectorManager;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.io.*;
import java.util.stream.Collectors;

public class TGBot {
    class parse_mode {
        static final String Markdown = "Markdown"; 
        static final String HTML = "HTML"; 
    }

    String apiToken;
    EventDetectorManager detector;
    long last_update_id = -1;

    public TGBot(String apiToken) {
        detector = new EventDetectorManager(epipe);
        this.apiToken = apiToken;
    }
    
    static public void setEventPipe(EventPipe pipe) {
        epipe = pipe;
    }
        
    static public EventPipe epipe;
    static final String empty_answer = new JSONObject().toString();
    
    public EventDetectorManager getDetectorManager() {
        return detector;
    }

    private enum Method {
        SEND,
        EDIT,
        UPDATES,
        GET_ME
    }

    class MethodCaller {
        final Map<String, String> requestParams = new HashMap<>();

        String decodeMethod(Method m) {
            switch(m) {
                case SEND: return "sendMessage";
                case EDIT: return "editMessageText";
                case UPDATES: return "getUpdates";
                case GET_ME: return "getMe";
            }
            return null;
        }

        String link;

        String encodeValue(String value) {
            try {
                return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
            }  catch(UnsupportedEncodingException e) {
                //to do (or not)
            }
            return new String("error occured");
        }
         
        void generateRequestLink(String methodName) {
            link = String.format("https://api.telegram.org/bot%s/%s", apiToken, methodName);
        } 

        public MethodCaller(String methodName) {
            generateRequestLink(methodName);
        }
        
        public MethodCaller(Method method) {   
            String methodName = decodeMethod(method);
            generateRequestLink(methodName);
        }

        public MethodCaller put(String name, String value) {
            requestParams.put(name, value);
            return this;
        }

        public String call() {
            String encodedURL = requestParams
                .keySet()
                .stream()
                .map(key -> key + "=" + encodeValue(requestParams.get(key)))
                .collect(Collectors.joining("&", link + "?", ""));

            try {
                return UsefulStuff.loadPage(encodedURL);
            } catch (Exception e) {
                return empty_answer;
            }
        }
    }

    public Message parseResponse(String text) {
        JSONObject result = (JSONObject)UsefulStuff
            .stringToJSON(text)
            .get("result");

        return new Message(result);
    }
    
    public Message sendMessage(Long chat_id, String text) {
        String page = new MethodCaller(Method.SEND)
            .put("chat_id", chat_id.toString())
            .put("text", text)
            .put("parse_mode", parse_mode.Markdown)
            .call();
        return parseResponse(page);
    }

    public Message sendMessage(Long chat_id, String text, Long reply_to_message_id) {
        String page = new MethodCaller(Method.SEND)
            .put("chat_id", chat_id.toString())
            .put("text", text)
            .put("parse_mode", parse_mode.Markdown)
            .put("reply_to_message_id", reply_to_message_id.toString())
            .call();
        return parseResponse(page);
    }

    public Message editMessage(Long chat_id, Long message_id, String text) {
        String page = new MethodCaller(Method.EDIT)
            .put("chat_id", chat_id.toString())
            .put("message_id", message_id.toString())
            .put("text", text)
            .call();

        return parseResponse(page);
    }

    public JSONObject loadLast() {
        String page = new MethodCaller(Method.UPDATES).call();
        return UsefulStuff.stringToJSON(page);
    }
    
    public Message.From getMe() {
        String page = new MethodCaller(Method.GET_ME).call();

        Object response = UsefulStuff
            .stringToJSON(page)
            .get("result");

        return new Message.From(response);
    }

    public interface Answerer {
        public void run(JSONObject message);
    }

    public JSONArray getUpdates() {
        JSONObject last = loadLast();

        JSONArray updates = (JSONArray)last.get("result"); 

        if(updates == null) {
            return new JSONArray();
        } 

        return updates;
    }

    boolean updateLast(JSONObject update) {
        long current = (long)update.get("update_id");

        if(last_update_id == current) {
            return true;
        }

        last_update_id = current;
        
        setOffset(current + 1l);
        return false;
    }

    public void start() {
        new TgBridge.BScheduler(() -> {
            for(Object updateObject: getUpdates()) {
                JSONObject update = (JSONObject)updateObject;
                this.updateLast(update);
                detector.handle(update);
            }
        }).schedule(0, 30L);
    }

    public void setOffset(Long offset) {
       // new Thread(() -> 
            new MethodCaller(Method.UPDATES)
                .put("offset", offset.toString())
                .call()
        //)
        ;
    }

};