package me.fulcanelly.tgbridge.tapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.io.InputStream;

import me.fulcanelly.clsql.stop.Stopable;
import me.fulcanelly.tgbridge.utils.UsefulStuff;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.eventbus.EventBus;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.stream.Collectors;

enum Method {
    SEND,
    EDIT,
    DELETE,
    UPDATES,
    GET_ME,
    PIN,
    GET_FILE
}

public class TGBot implements Stopable {

    private ArrayList<Long> recent_updates = new ArrayList<>();
    static int MAX_RECENT_UPDATES = 10;

    /**
     * @return true if update not handlet yet
     *         otherwise return false and add update to list
     *         also keep list fixed size
     * 
     *         needed to prevent message repeating
     * 
     *         todo
     */
    boolean updateWatcher(Long update_id) {
        boolean result = true;

        synchronized (recent_updates) {
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
    // EventDetectorManager<JSONObject, TGBot> detector;
    long last_update_id = -1;

    EventBus bus;

    public TGBot(String apiToken, EventBus bus) {
        // detector = new EventDetectorManager<>(pipe);
        this.bus = bus;
        this.apiToken = apiToken;
    }

    // public EventDetectorManager<JSONObject, TGBot> getDetectorManager() {
    // return detector;
    // }

    class MethodCaller {

        final Map<String, String> requestParams = new HashMap<>();

        String decodeMethod(Method m) {
            switch (m) {
                case SEND:
                    return "sendMessage";
                case EDIT:
                    return "editMessageText";
                case UPDATES:
                    return "getUpdates";
                case GET_ME:
                    return "getMe";
                case PIN:
                    return "pinChatMessage";
                case DELETE:
                    return "deleteMessage";
                case GET_FILE:
                    return "getFile";
            }
            throw new RuntimeException("Unknown method");
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

        boolean logging = false;

        public MethodCaller enableLogging() {
            logging = true;
            return this;
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
            if (logging) {
                System.out.println("encodedURL: " + encodedURL);
            }
            try {
                return UsefulStuff.loadPage(encodedURL);
            } catch (Exception e) {
                e.printStackTrace();
                return "{}";
            }
        }
    }

    public Message parseResponse(String text) {
        JSONObject result = (JSONObject) UsefulStuff
                .stringToJSON(text)
                .get("result");

        return new Message(result, this);
    }

    // todo
    public boolean pinChatMessage(String chat_id, String message_id, Boolean dont_notificate) {
        new MethodCaller(Method.PIN)
                .put("disable_notification", dont_notificate.toString())
                .put("chat_id", chat_id)
                .put("message_id", message_id).call();
        return true;
    }

    public boolean pinChatMessage(String chat_id, String message_id) {
        return pinChatMessage(chat_id, message_id, true);
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

    public void deleteMessage(Long chat_id, Long message_id) {
        new MethodCaller(Method.DELETE)
                .put("message_id", message_id.toString())
                .put("chat_id", chat_id.toString())
                .call();
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

    public From getMe() {
        String page = new MethodCaller(Method.GET_ME).call();

        Object response = UsefulStuff
                .stringToJSON(page)
                .get("result");

        return new From(response);
    }

    public interface Answerer {
        public void run(JSONObject message);
    }

    public JSONArray getUpdates() {
        JSONObject last = loadLast();

        JSONArray updates = (JSONArray) last.get("result");

        if (updates == null) {
            return new JSONArray();
        }

        return updates;
    }

    public Optional<TgFile> getFile(String fileId) {
        var result = new MethodCaller(Method.GET_FILE)
                .enableLogging()
                .put("file_id", fileId).call();

        var json = UsefulStuff.stringToJSON(result).get("result");
        return Optional.ofNullable((JSONObject) json).map(TgFile::new);
    }

    @SneakyThrows
    public InputStream loadFile(String filePath) {
        return UsefulStuff.loadFileHTTPS(
                String.format("https://api.telegram.org/file/bot%s/%s", apiToken, filePath));
    }

    boolean updateLast(JSONObject update) {
        long current = (long) update.get("update_id");

        if (last_update_id == current) {
            return true;
        }

        last_update_id = current;

        setOffset(current + 1l);
        return false;
    }

    boolean alive = true;

    @Override
    public void stopIt() {
        alive = false;
    }

    @SneakyThrows
    void loop(Runnable func) {
        while (true && alive) {
            func.run();
            Thread.sleep(30);
        }
    }

    public void start() {
        Runnable runnable = () -> loop(this::updater);
        new Thread(runnable).start();
    }

    void updater() {
        for (Object updateObject : getUpdates()) {
            JSONObject update = (JSONObject) updateObject;
            updateWatcher((Long) update.get("update_id"));
            updateLast(update);

            Object message = update.get("message");

            if (message != null) {
                bus.post(new Message(message, this));
            }

        }
    }

    public void setOffset(Long offset) {
        new MethodCaller(Method.UPDATES)
                .put("offset", offset.toString())
                .call();
    }

}
