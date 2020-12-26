package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;

import com.google.gson.JsonArray;
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
        List<String> params = serverRequest.getParams();
        Log log;

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
                if(leaderIndex != context.getLogIndex()) {
                    SendMessageUtils.inconsistentLog(context, request);
                } else {
                    log = JSONUtils.readLogFile(context.getLogName());
                    Entry lastEntry = log.getLastEntry();

                    if(lastEntry != null && !lastEntry.isCommited()){
                        lastEntry.commit();
                    }
                }
                break;

            case Constants.APPEND:
                // 1º get log
                log = JSONUtils.readLogFile(context.getLogName());
                int logIndex = context.getLogIndex();

                // 2º create new entry and append
                int index = Integer.parseInt(params.get(0));
                int term = Integer.parseInt(params.get(1));
                int id = Integer.parseInt(params.get(2));
                int value = Integer.parseInt(params.get(3));

                // Si mi next index no coincide con el del leader me tengo que actualizar
                Boolean consistent = logIndex == (index-1);
                if(consistent) {
                    Entry entry = new Entry(index, term, id, value);
                    log.appendEntry(entry);
                    JSONUtils.writeLogFile(context.getLogName(), log.toJson());
                }

                SendMessageUtils.appendEntryResponse(context, request, consistent, logIndex);


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
