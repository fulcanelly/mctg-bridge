

package me.fulcanelly.tgbridge.tapi;

//import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
//import org.json.simple.parser.*; 
//import org.json.simple.parser.JSONParser;

public class Message {

    static public class From  {
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

    public JSONObject msg = new JSONObject();

    static public TGBot bot;

    public <T>Message(T msg) {
        this.msg = (JSONObject)msg;
    }

    static public void setBot(TGBot b) {
        bot = b;
    }

    public Message getReplyTo() {
        return new Message(msg.get("reply_to_message"));
    }
    
    public boolean is_null() {
        return msg == null;
    }

    public boolean isText() {
        return msg.get("text") != null;
    }

    public boolean isMedia() {
        return getCaption() != null;
    }

    public boolean isVoice() {
        return  msg.get("voice") != null;
    }

    public Long getMsgId()
    {
        return (Long)msg.get("message_id");
    }
/*
    public void setMsgId(Long msg_id) {
        msg.put("message_id", msg_id);
    }
*/
    public String getText() {
        return (String)msg.get("text");
    }

    public String getCaption() {
        return (String)msg.get("caption");
    } 

    public From getFrom() {
        return new From(msg.get("from"));
    }


    public From getChat() {
        return new From(msg.get("chat"));
    }

    /*
    public void setChat(From chat) {
        msg.put("chat", chat.from);
    }
*/
    public Message reply(String text) {
        return bot.sendMessage(getChat().getId(), text, getMsgId());
    }

    public Message edit(String newText) {
        return bot.editMessage(
            getChat().getId(), 
            getMsgId(), 
            newText
        );
    }
}
