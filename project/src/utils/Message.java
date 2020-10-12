package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;

public class Message {

    private int term;
    private String type;
    private int from;
    private int to;
    private List<String> params;

    public Message(int term, String type, int from, int to, List<String> params) {
        this.term = term;
        this.type = type;
        this.from = from;
        this.to = to;
        this.params = params;
    }

    public int getTerm() {
        return this.term;
    }

    public String getType() {
        return this.type;
    }

    public int getFrom() {
        return this.from;
    }

    public int getTo() {
        return this.to;
    }

    public List<String> getParams() {
        return this.params;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this, Message.class);
    }

    public void log(int localPort, Boolean received) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        if (received) {
            System.out.println("Message Received - Type: " + this.type);
        } else {
            System.out.println("Message Sent - Type: " + this.type);
        }

        if (localPort == from) {
            System.out.println("Sent to: " + this.to + " [" + dtf.format(now) + "]");
        } else {
            System.out.println("Received from: " + this.from + " [" + dtf.format(now) + "]");
        }
        if( params != null) {
            System.out.print("Params: ");
            for (String param : params) {
                System.out.print(param + " ");
            }
        }
        System.out.println("\n\n--------------------------------------------------------------\n");
    }

}
