package utils;

import messages.HeartBeat;
import messages.Message;
import org.json.simple.JSONObject;

import java.net.DatagramPacket;

public final class UDPUtils {

    public UDPUtils() {
    }

    public static void logMessageArrive(DatagramPacket datagramPacket, Message message, int port) {
        System.out.println("Datagram received in raft node " + port + " from host: " + datagramPacket.getAddress()
                + " and port: " + datagramPacket.getPort());
        JSONObject messageContent = message.getMessageAsJson();
        switch (message.getType()) {
        case Constants.HEART_BEAT_MESSAGE:
            System.out.println(
                    "HeartBeat Message, from: " + messageContent.get("from") + "to: " + messageContent.get("to"));
            break;
        case Constants.CLIENT_MESSAGE:
            System.out.println("Client Message");
            break;
        case Constants.SERVER_MESSAGE:
            System.out.println("Server Message");
            break;
        }
    }
}
