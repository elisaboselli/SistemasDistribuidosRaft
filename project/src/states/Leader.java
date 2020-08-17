package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Timer;
import java.util.TimerTask;

import context.Context;
import utils.Message;
import utils.Constants;
import utils.Host;

public class Leader {

    static State execute(Context context) {
        // try {
        // Datagram Socket
        // byte[] buffer = new byte[1000];
        // Heart Beat Sender
        Timer timer = new Timer();
        TimerTask hearbeat = new TimerTask() {
            @Override
            public void run() {
                Leader.sendHeartBeat(context);
            }
        };
        timer.scheduleAtFixedRate(hearbeat, 0, Constants.HEART_BEAT);
        // TODO puede llegar un mensaje de un candidato con un termino mayor
        // TODO puede
        /*
         * while (true) { // Receive a message DatagramPacket receivedMessage = new
         * DatagramPacket(buffer, buffer.length);
         * context.getServerSocket().receive(receivedMessage);
         * 
         * Message message = JSONUtils.getMessage(receivedMessage);
         * UDPUtils.logMessageArrive(receivedMessage, message, context.getPort()); } }
         * catch (SocketException e) { System.out.println("Socket: " + e.getMessage());
         * } catch (IOException e) { System.out.println("IO: " + e.getMessage()); }
         */

        while (true) {

        }

        // return newEvent();
    }

    static State newEvent() {
        return null;
    }

    public static void sendHeartBeat(Context context) {
        try {
            for (Host host : context.getAllHosts()) {
                Message heartBeatMessage = new Message(0, Constants.HEART_BEAT_MESSAGE, context.getPort(),
                        host.getPort(), null);
                DatagramPacket heartBeat = new DatagramPacket(heartBeatMessage.toJson().getBytes(),
                        heartBeatMessage.toJson().length(), host.getAddress(), host.getPort());
                context.getServerSocket().send(heartBeat);
                System.out.println("Send heartbeat to " + host.getPort());
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
        System.out.println("");
    }

}
