package example;

import com.google.gson.Gson;

public class MessageExample {

    private int term;
    private String type;
    private int from;
    private int to;
    private String jsonMessage;

    public MessageExample(int term, String type, int from, int to, String json) {
        this.term = term;
        this.type = type;
        this.from = from;
        this.to = to;
        this.jsonMessage = json;
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

    public String getJsonMessage() {
        return this.jsonMessage;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this, MessageExample.class);
    }

    public void log(int localPort) {
        if (localPort == from) {
            System.out.println("Message Sent to: " + this.to);
        } else {
            System.out.println("Message Received from: " + this.from);
        }
        System.out.println(this.jsonMessage);
        System.out.println("\n--------------------------------------------------------------\n");

    }
}
