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

        System.out.println("------------------------------- LEADER -------------------------------");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("START >>  [" + dtf.format(now) + "]\n");

        context.setLeader(null);
        context.show(Constants.LEADER);

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

    public static void processMessage(Context context, DatagramPacket request) {

        Message serverRequest = JSONUtils.messageFromJson(request);
        serverRequest.log(context.getPort(), true, context.getLogName());
        List<String> params = serverRequest.getParams();
        Storage storage;
        Entry entry;
        int id, value;

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

            case Constants.GET:
                // 1º Get storage and params
                storage = JSONUtils.readStorageFile(context.getStorageName());
                id = Integer.parseInt(params.get(0));

                // 2º Search Entry
                entry = storage.getCommitedEntryById(id);

                // 3º Send Response
                SendMessageUtils.sendResponseGetMessage(context,request, entry, id);
                break;

            case Constants.SET:
                // 1º get storage
                storage = JSONUtils.readStorageFile(context.getStorageName());

                // 2º create new entry and append
                id = Integer.parseInt(params.get(0));
                value = Integer.parseInt(params.get(1));

                entry = new Entry(context, id, value);
                storage.appendEntry(entry);
                JSONUtils.writeStorageFile(context.getStorageName(), storage.toJsonArray());
                context.updateLogIndex();

                // 3º ask followers to append
                SendMessageUtils.appendEntry(context, entry);
                SendMessageUtils.acceptSetMessage(context, request, entry);
                break;

            case Constants.APPEND_SUCCESS:
                storage = JSONUtils.readStorageFile(context.getStorageName());
                id = Integer.parseInt(params.get(0));
                entry = storage.getNewestEntryById(id);

                entry.updateQuorum();

                if(entry.getQuorum() == Constants.QUORUM) {
                    entry.commit();
                    SendMessageUtils.commitEntry(context, id);
                }

                JSONUtils.writeStorageFile(context.getStorageName(), storage.toJsonArray());

                break;

            case Constants.INCONSISTENT_LOG:

                int followerIndex = Integer.parseInt(params.get(0));
                storage = JSONUtils.readStorageFile(context.getStorageName());

                entry = storage.getEntryByIndex(followerIndex+1);
                SendMessageUtils.updateInconsistentLog(context, request, entry);

                break;


            default:
        }
    }

}
