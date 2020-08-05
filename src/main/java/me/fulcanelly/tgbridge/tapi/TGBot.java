package me.fulcanelly.tgbridge.tapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.fulcanelly.tgbridge.utils.UsefulStuff;
import me.fulcanelly.tgbridge.utils.events.detector.EventDetectorManager;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.stream.Collectors;

enum Method {
    SEND,
    EDIT,
    UPDATES,
    GET_ME
}

public class TGBot {
    

    private ArrayList<Long> recent_updates = new ArrayList<>();
    static int MAX_RECENT_UPDATES = 10;
    
    /**
     * @return true if update not handlet yet
     * otherwise return false and add update to list
     * also keep list fixed size
     * 
     * needed to prevent message repeating
     * 
     * todo
     */
    boolean updateWatcher(Long update_id) {
        boolean result = true;

        synchronized(recent_updates) {
            adjustRecentListSize();

            if (recent_updates.contains(update_id)) {
                result = false;
            }

            recent_updates.add(update_id);
        }

        return result;
    }

    void adjustRecentListSize() {
        while (recent_updates.size() > MAX_RECENT_UPDATES) {
            recent_updates.remove(0);
        }
    }
 
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
    
    public EventDetectorManager getDetectorManager() {
        return detector;
    }

    static final String empty_answer = new JSONObject().toString();

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

        @SneakyThrows
        String encodeValue(String value) {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
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
            String encodedURL = requestParams.keySet()
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
    
    MethodCaller defaultCaller(Method method, String text, Long chat_id) {
        return new MethodCaller(method)
            .put("parse_mode", parse_mode.Markdown)
            .put("chat_id", chat_id.toString())
            .put("text", text);
    }

    public Message sendMessage(Long chat_id, String text) {
        String page = defaultCaller(Method.SEND, text, chat_id).call();
        return parseResponse(page);
    }

    public Message sendMessage(Long chat_id, String text, Long reply_to_message_id) {
        String page = defaultCaller(Method.SEND, text, chat_id)
            .put("reply_to_message_id", reply_to_message_id.toString()).call();

        return parseResponse(page);
    }

    public Message editMessage(Long chat_id, Long message_id, String text) {
        String page = defaultCaller(Method.EDIT, text, chat_id)
            .put("message_id", message_id.toString()).call();

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

    boolean alive = true;

    public void stop() {
        alive = false;
    }

    @SneakyThrows
    void setup(Runnable func) {
        while(true && alive) {
            func.run();
            Thread.sleep(30);
        }
    }

    public void start() {
        Runnable runnable = () -> setup(this::updater);
        new Thread(runnable).start();    
    }

    void updater() {
        for(Object updateObject: getUpdates()) {
            JSONObject update = (JSONObject)updateObject;
            updateWatcher((Long)update.get("update_id"));
            this.updateLast(update);
            detector.handle(update);
        } 
    }
    
    public void setOffset(Long offset) {
        new MethodCaller(Method.UPDATES)
            .put("offset", offset.toString())
            .call();
    }

};
