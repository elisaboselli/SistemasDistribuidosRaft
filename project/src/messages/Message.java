package messages;

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

    public void log(int localPort) {
        System.out.println(this.type);
        if (localPort == from) {
            System.out.println("Sent to: " + this.to);
        } else {
            System.out.println("Received from: " + this.from);
        }
        System.out.print("Params: ");
        for (String param : params) {
            System.out.print(param + " ");
        }
        System.out.println("\n\n--------------------------------------------------------------\n");
    }

}
