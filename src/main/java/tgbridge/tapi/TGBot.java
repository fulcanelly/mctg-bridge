package tgbridge.tapi;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import tgbridge.utils.UsefulStuff;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.io.*;
import java.util.stream.Collectors;

interface Bot {
    public Message sendMessage(Long chat_id, String text, Long reply_to_message_id);
}

public class TGBot implements Bot {
    String apiToken;

    public TGBot(String apiToken) {
        this.apiToken = apiToken;
    }
        
    static final String empty_answer = new JSONObject().toString();

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
            .call();
        return parseResponse(page);
    }

    public Message sendMessage(Long chat_id, String text, Long reply_to_message_id) {
        String page = new MethodCaller(Method.SEND)
            .put("chat_id", chat_id.toString())
            .put("text", text)
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

    public void getLastMessages(Answerer onMsg) {
        JSONObject last = loadLast();

        JSONArray updates = (JSONArray)last.get("result"); 

        if(updates == null) {
            return;
        }

        for(Object update_obj: updates) {
            JSONObject update = (JSONObject)update_obj;
            
            setOffset((long)update.get("update_id") + 1);

            Object message = update.get("message");
            //"edited_message"
            if(message != null) {
                onMsg.run((JSONObject)message);
            }
        }
    }

    public void setOffset(Long offset) {
        new MethodCaller(Method.UPDATES)
            .put("offset", offset.toString())
            .call();
    }

    public static interface Listeners {
      //  to do
    }
};