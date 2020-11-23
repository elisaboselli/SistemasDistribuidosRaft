package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;

import context.Context;
import utils.*;

import static utils.JSONUtils.parseDatagramPacket;

public class Follower {

    private static int timeout = 10000;

    static State execute(Context context) {
        System.out.println("\n-------------------------- FOLLOWER --------------------------");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("START >>  [" + dtf.format(now) + "]\n");

        DatagramSocket socket = context.getServerSocket();
        try {
            socket.setSoTimeout(context.getTimeout());
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Wait for messages
        while (true) {
            byte[] buffer = new byte[1000];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(request);
                processMessage(context, request);
            } catch (SocketTimeoutException e) {
                dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                now = LocalDateTime.now();
                System.out.println("timeout >>  [" + dtf.format(now) + "]");
                return State.CANDIDATE;
            } catch (IOException e) {
                e.printStackTrace();
                return State.FOLLOWER;
            }
        }
    }

    private static void processMessage(Context context, DatagramPacket request) {

        Message serverRequest = JSONUtils.messageFromJson(request);
        serverRequest.log(context.getPort(), true);

        switch (serverRequest.getType()) {

            // Process postulation message
            case Constants.POSTULATION:
                SendMessageUtils.sendVote(context, request, serverRequest.getTerm());
                break;

            // Process heartbeat message
            case Constants.HEART_BEAT_MESSAGE:
                Host leaderHost = new Host(request.getAddress(), request.getPort());
                context.setLeader(leaderHost);
                //System.out.println("New Leader >> " + context.getLeader().toString());
                break;

            // Process client message
            case Constants.CLIENT_MESSAGE:
                // TODO: Pasar el mensaje al lider o responder con la ip del lider
                break;

            case Constants.CLIENT_SET_MESSAGE:
                // TODO: Responder con la ip del lider
                SendMessageUtils.rejectSetMessage(context, request);
                break;

            case Constants.CLIENT_GET_MESSAGE:
                // TODO: Acá debería responder ?
                break;

            // TODO: Other messages
            default:
                //Process message
        }
    }
}
