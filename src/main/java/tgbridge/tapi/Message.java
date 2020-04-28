

package tgbridge.tapi;

//import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
//import org.json.simple.parser.*; 
//import org.json.simple.parser.JSONParser;

public class Message {

    static public class From  {
        /**
         *
         */

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
    }

    public JSONObject msg = new JSONObject();
    //public TGBot bot;
    static public TGBot bot;

    public Message(JSONObject msg) {
        this.msg = msg;
    }

    static public void setBot(TGBot b) {
        bot = b;
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

    public void setMsgId(Long msg_id) {
        msg.put("message_id", msg_id);
    }

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

    public void setChat(From chat) {
        msg.put("chat", chat.from);
    }

    public Message reply(String text) {
        return bot
            .sendMessage(getChat().getId(), text, getMsgId());
    }

    public Message edit(String newText) {
        return bot.editMessage(
            getChat().getId(), 
            getMsgId(), 
            newText
        );
    }
}
