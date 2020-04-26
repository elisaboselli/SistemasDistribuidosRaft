package messages;

import com.google.gson.Gson;

public class ServerMessage {

    private String type;
    private int id;
    private int value;

    public ServerMessage() {
    }

    public void setType(String _type) {
        this.type = _type;
    }

    public void setId(int _id) {
        this.id = _id;
    }

    public void setValue(int _value) {
        this.value = _value;
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this, ServerMessage.class);
    }

}