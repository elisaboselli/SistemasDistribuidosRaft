package utils;

import java.net.DatagramPacket;

import messages.Message;

public final class UDPUtils {

    public UDPUtils() {
    }

    public static void logMessageArrive(DatagramPacket datagramPacket, Message message, int port) {
        System.out.println("Datagram received in raft node " + port + " from host: " + datagramPacket.getAddress()
                + " and port: " + datagramPacket.getPort());
        // JsonObject messageContent = (JsonObject) message.getMessageAsJson();
        switch (message.getType()) {
        case Constants.HEART_BEAT_MESSAGE:
            System.out.println("HeartBeat Message");
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
