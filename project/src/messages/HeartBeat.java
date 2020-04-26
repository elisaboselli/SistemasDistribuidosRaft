package messages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        return gson.toJson(this, HeartBeat.class);
    }

    public void log(int localPort) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        if (localPort == from) {
            System.out.println("HeartBeat sent to: " + this.to + " [" + dtf.format(now) + "]");
        } else {
            System.out.println("HeartBeat received from: " + this.from + " [" + dtf.format(now) + "]");
        }
        System.out.println("\n--------------------------------------------------------------\n");

    }

}
