package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

public class Message {

    public int term;
    private String type;
    private int portFrom;
    private String addressFrom;
    private int portTo;
    private String addressTo;
    private List<String> params;

    private final String separator = "----------------------------------------------------------------------";

    public Message(int term, String type, int portFrom, String addressFrom, int portTo, String addressTo, List<String> params) {
        this.term = term;
        this.type = type;
        this.portFrom = portFrom;
        this.addressFrom = addressFrom;
        this.portTo = portTo;
        this.addressTo = addressTo;
        this.params = params;
    }

    public Message(String params){
        this.params = Arrays.asList(params);
    }

    public int getTerm() {
        return this.term;
    }

    public String getType() {
        return this.type;
    }

    public int getPortFrom() {
        return this.portFrom;
    }

    public int getPortTo() {
        return this.portTo;
    }

    public List<String> getParams() {
        return this.params;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this, Message.class);
    }

    public void log(int localPort, Boolean received, String fileName) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        List<String> log = new ArrayList<>();

        if(!this.type.equals(Constants.HEART_BEAT_MESSAGE)) {

            System.out.println(separator + "\n");
            log.add(separator);

            // Message type
            String msgType = received ? ("Message Received - Type: " + this.type) : ("Message Sent - Type: " + this.type);
            System.out.println(msgType);
            log.add(msgType);

            // Sent or received
            String sentReceived = localPort == portFrom ? ("Sent to: " + this.addressTo + ":" + this.portTo) : ("Received from: " + this.addressFrom + ":"  + this.portFrom);
            sentReceived = sentReceived.concat(" [" + dtf.format(now) + "]");
            System.out.println(sentReceived);
            log.add(sentReceived);

            // Params
            String logParams = "Params: ";
            if (params != null) {
                for (String param : params) {
                    logParams = logParams.concat(param + " ");
                }
            }
            System.out.print(logParams);
            log.add(logParams);

            System.out.print("\n\n");

            if (!fileName.isEmpty()) {
                JSONUtils.writeLogFile(fileName, log);
            }
        }
    }

}
