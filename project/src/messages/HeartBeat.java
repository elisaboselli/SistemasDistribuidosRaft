package messages;

import com.google.gson.Gson;
import utils.Constants;

public class HeartBeat {
    private String type;
    private int from;
    private int to;

    public HeartBeat(int _from, int _to) {
        this.type = Constants.HEART_BEAT_MESSAGE;
        this.from = _from;
        this.to = _to;
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this, ClientMessage.class);
    }

}
