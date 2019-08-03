package messages;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Message {

    private String type;
    private String jsonMessage;

    public Message(String _type, String _json){
        this.type = _type;
        this.jsonMessage = _json;
    }

    public String getType(){
        return this.type;
    }

    public String getJsonMessage(){
        return this.jsonMessage;
    }

    public JSONObject getMessageAsJson(){
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(this.jsonMessage);
        } catch (ParseException e) {
            System.out.println("Error while parsing json from message");
            e.printStackTrace();
        }
        return json;
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this, Message.class);
    }
}
