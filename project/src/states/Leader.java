package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import context.Context;
import utils.*;

public class Leader {

    private static String state = "leader";

    static State execute(Context context) {

        System.out.println("--------------------------- LEADER ---------------------------");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("START >>  [" + dtf.format(now) + "]\n");

        context.show();

        for (Host host : context.getAllHosts()) {
            System.out.println(">> Host: " + host.getPort());
        }


        Timer timer = new Timer();
        TimerTask hearbeat = new TimerTask() {
            @Override
            public void run() {
                SendMessageUtils.sendHeartBeat(context);
            }
        };
        timer.scheduleAtFixedRate(hearbeat, 0, Constants.HEART_BEAT);

        DatagramSocket socket = context.getServerSocket();
        try {
            socket.setSoTimeout(context.getTimeout());
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Wait for messages
        while (state.equals("leader")){
            byte[] buffer = new byte[1000];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(request);
                processMessage(context, request);
            } catch (IOException e) {
                System.out.println("IO Exception: " + e.getMessage());
            }
        }

        if(state.equals("follower")) {
            return State.FOLLOWER;
        }

        if(state.equals("candidate")) {
            return State.CANDIDATE;
        }

        return null;
    }

    private static void processMessage(Context context, DatagramPacket request) {

        Message serverRequest = JSONUtils.messageFromJson(request);
        serverRequest.log(context.getPort(), true);

        switch (serverRequest.getType()) {

            case Constants.POSTULATION:
                SendMessageUtils.sendVote(context, request, serverRequest.getTerm());
                state = "follower";
                break;

            case Constants.HEART_BEAT_MESSAGE:
                if (serverRequest.getTerm() >= context.getTerm()) {
                    context.setTerm(serverRequest.getTerm());
                    state = "follower";
                }
                break;

            default:
        }
    }

}
