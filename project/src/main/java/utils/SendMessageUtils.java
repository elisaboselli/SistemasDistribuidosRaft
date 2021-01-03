package utils;

import context.Context;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SendMessageUtils {

    static private void sendMessage(Context context, Host hostTo, String messageType, List<String> messageParams) {
        try {

            // Prepare message
            Message message = new Message(context.getTerm(), messageType, context.getPort(),
                    hostTo.getPort(), messageParams);

            // Prepare datagram packet
            String messageStr = message.toJson();
            DatagramPacket messageDP = new DatagramPacket(messageStr.getBytes(), messageStr.length(),
                    hostTo.getAddress(), hostTo.getPort());

            // Send response
            context.getServerSocket().send(messageDP);
            message.log(context.getPort(), false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendHeartBeat(Context context) {
        List<String> messageParams = Arrays.asList(String.valueOf(context.getLogIndex()));
        for (Host host : context.getAllHosts()) {
            sendMessage(context, host, Constants.HEART_BEAT_MESSAGE, messageParams);
        }
    }

    public static void sendPostulation(List<Host> voters, Context context) {
        for (Host voter : voters) {
            sendMessage(context, voter, Constants.POSTULATION, null);
        }
    }

    public static void sendVote(Context context, DatagramPacket voteRequest, int requestTerm) {
        // TODO: Check requestTerm >= contextTerm && requestIndex >= contextIndex

        // Update term if needed
        boolean isPositiveVote = context.getTerm() < requestTerm;
        if (isPositiveVote) {
            context.setTerm(requestTerm);
            Host leaderHost = new Host(voteRequest.getAddress(), voteRequest.getPort());
            context.setLeader(leaderHost);
            context.show();
        }

        // Prepare & send response
        String messageType = isPositiveVote ? Constants.VOTE_OK : Constants.VOTE_REJECT;
        List<String> messageParams = Arrays.asList(messageType + " for term " + requestTerm);
        Host host = new Host(voteRequest.getAddress(), voteRequest.getPort());
        sendMessage(context, host, messageType, messageParams);
    }

    public static void rejectSetMessage(Context context, DatagramPacket request) {
        List<String> messageParams = Arrays.asList(context.getLeader().getAddress().toString(),
                String.valueOf(context.getLeader().getPort()));
        Host host = new Host(request.getAddress(), request.getPort());
        sendMessage(context, host, Constants.NOT_LEADER, messageParams);
    }

    public static void appendEntry(Context context, Entry entry) {
        for (Host host : context.getAllHosts()) {
            List<String> messageParams = Arrays.asList(entry.getIndexStr(), entry.getTermStr(), entry.getIdStr(),
                    entry.getValueStr());
            sendMessage(context, host, Constants.APPEND, messageParams);
        }
    }

    public static void appendEntryResponse(Context context, DatagramPacket request, Boolean success, int lastIndex,
                                           Boolean wasInconsistentLog) {
        Host host = new Host(request.getAddress(), request.getPort());
        if(success){
            if(!wasInconsistentLog){
                sendMessage(context, host, Constants.APPEND_SUCCESS, null);
            } else {
                List<String> messageParams = Arrays.asList(String.valueOf(context.getLogIndex()));
                sendMessage(context, host, Constants.INCONSISTENT_LOG, messageParams);
            }
        } else {
            List<String> messageParams = Collections.singletonList(String.valueOf(lastIndex));
            sendMessage(context, host, Constants.APPEND_FAIL, messageParams);
        }
    }

    public static void inconsistentLog(Context context, DatagramPacket request) {
        Host host = new Host(request.getAddress(), request.getPort());
        List<String> messageParams = Arrays.asList(String.valueOf(context.getLogIndex()));
        sendMessage(context, host, Constants.INCONSISTENT_LOG, messageParams);
    }

    public static void updateInconsistentLog(Context context, DatagramPacket request, Entry entry) {
        Host host = new Host(request.getAddress(), request.getPort());
        List<String> messageParams =Arrays.asList(entry.getIndexStr(), entry.getTermStr(), entry.getIdStr(),
                entry.getValueStr(), Constants.UPDATE);
        sendMessage(context, host, Constants.APPEND, messageParams);
    }
}
