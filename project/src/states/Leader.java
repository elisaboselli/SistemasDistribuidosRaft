package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

        context.setLeader(null);
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
        Log log;

        switch (serverRequest.getType()) {

            case Constants.POSTULATION:
                SendMessageUtils.sendVote(context, request, serverRequest.getTerm());
                state = "follower";
                break;

            case Constants.HEART_BEAT_MESSAGE:
                if (serverRequest.getTerm() >= context.getTerm()) {
                    Host leaderHost = new Host(request.getAddress(), request.getPort());
                    context.setLeader(leaderHost);
                    context.setTerm(serverRequest.getTerm());
                    state = "follower";
                }
                break;

            case Constants.SET:
                // 1º get log
                log = JSONUtils.readLogFile(context.getLogName());

                // 2º create new entry and append
                List<String> params = serverRequest.getParams();
                int id = Integer.parseInt(params.get(0));
                int value = Integer.parseInt(params.get(1));

                Entry entry = new Entry(context, id, value);
                log.appendEntry(entry);
                JSONUtils.writeLogFile(context.getLogName(), log.toJson());

                // 3º ask followers to append
                SendMessageUtils.appendEntry(context, entry);
                break;

            case Constants.APPEND_SUCCESS:
                log = JSONUtils.readLogFile(context.getLogName());
                Entry lastEntry = log.getEntryList().get(0);
                lastEntry.updateQuorum();

                if(lastEntry.getQuorum() >= Constants.QUORUM) {
                    lastEntry.commit();
                }

                break;


            default:
        }
    }

}
