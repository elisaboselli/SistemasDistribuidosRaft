package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import context.Context;
import utils.*;

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
        serverRequest.log(context.getPort(), true, context.getLogName());
        List<String> params = serverRequest.getParams();
        Storage storage;

        switch (serverRequest.getType()) {

            // Process postulation message
            case Constants.POSTULATION:
                SendMessageUtils.sendVote(context, request, serverRequest.getTerm());
                break;

            // Process heartbeat message
            case Constants.HEART_BEAT_MESSAGE:
                Host leaderHost = new Host(request.getAddress(), request.getPort());
                context.setLeader(leaderHost);

                int leaderIndex = Integer.parseInt(params.get(0));
                if(leaderIndex != context.getStorageIndex()) {
                    SendMessageUtils.inconsistentLog(context, request);
                } else {
                    storage = JSONUtils.readStorageFile(context.getStorageName());
                    Entry lastEntry = storage.getLastEntry();

                    if(lastEntry != null && !lastEntry.isCommited()){
                        lastEntry.commit();
                        JSONUtils.writeStorageFile(context.getStorageName(), storage.toJson());
                    }
                }
                break;

            case Constants.APPEND:
                // 1º get storage
                storage = JSONUtils.readStorageFile(context.getStorageName());
                int logIndex = context.getStorageIndex();

                // 2º create new entry and append
                int index = Integer.parseInt(params.get(0));
                int term = Integer.parseInt(params.get(1));
                int id = Integer.parseInt(params.get(2));
                int value = Integer.parseInt(params.get(3));

                boolean inconsistent_log = false;
                if (params.size()>4) {
                    inconsistent_log = params.get(4) != null;
                }

                // Si los storage estan consistententes hasta el momento, agrego la nueva entrada.
                Boolean consistent = logIndex == (index-1);
                if(consistent) {
                    Entry entry = new Entry(term, index, id, value);
                    storage.appendEntry(entry);
                    JSONUtils.writeStorageFile(context.getStorageName(), storage.toJson());
                    context.updateLogIndex();
                }

                // Y respondo el append
                SendMessageUtils.appendEntryResponse(context, request, consistent, logIndex+1, inconsistent_log);


                break;

            case Constants.SET:
                // TODO: Pasar el mensaje al lider o responder con la ip del lider
                SendMessageUtils.rejectSetMessage(context, request);
                break;

            case Constants.GET:
                // TODO: Acá debería responder ?
                break;

            // TODO: Other messages
            default:
                //Process message
        }
    }
}
