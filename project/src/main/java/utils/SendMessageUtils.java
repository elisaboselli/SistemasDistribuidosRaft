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
            Message message = new Message(context.getTerm(), messageType, context.getPort(), context.getAddress(),
                    hostTo.getPort(), hostTo.getAddress().toString(), messageParams);

            // Prepare datagram packet
            String messageStr = message.toJson();
            DatagramPacket messageDP = new DatagramPacket(messageStr.getBytes(), messageStr.length(),
                    hostTo.getAddress(), hostTo.getPort());

            // Send response
            context.getServerSocket().send(messageDP);
            message.log(context.getPort(), false, context.getLogName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // SERVERS MESSAGES -------------------------------------------------------------------------

    public static void sendHeartBeat(Context context) {
        List<String> messageParams = Arrays.asList(String.valueOf(context.getTerm()), String.valueOf(context.getStorageIndex()));
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

        // Update term if needed
        boolean isPositiveVote = context.getTerm() < requestTerm;
        if (isPositiveVote) {
            context.setTerm(requestTerm);
            Host leaderHost = new Host(voteRequest.getAddress(), voteRequest.getPort());
            context.setLeader(leaderHost);
            context.show(Constants.FOLLOWER);
        }

        // Prepare & send response
        String messageType = isPositiveVote ? Constants.VOTE_OK : Constants.VOTE_REJECT;
        List<String> messageParams = Arrays.asList(messageType + " for term " + requestTerm);
        Host host = new Host(voteRequest.getAddress(), voteRequest.getPort());
        sendMessage(context, host, messageType, messageParams);
    }

    public static void appendEntry(Context context, Entry entry) {
        for (Host host : context.getAllHosts()) {
            List<String> messageParams = Arrays.asList(entry.indexStr(), entry.termStr(), entry.idStr(),
                    entry.valueStr());
            sendMessage(context, host, Constants.APPEND, messageParams);
        }
    }

    public static void commitEntry(Context context, int id) {
        for (Host host : context.getAllHosts()) {
            List<String> messageParams = Collections.singletonList(String.valueOf(id));
            sendMessage(context, host, Constants.COMMIT, messageParams);
        }
    }

    public static void appendEntryResponse(Context context, DatagramPacket request, Boolean success, int id, int lastIndex, Boolean inconsistentLog) {
        Host host = new Host(request.getAddress(), request.getPort());
        List<String> messageParams;
        if(success){
            if (inconsistentLog) {
                sendMessage(context, host, Constants.UPDATE_SUCCESS, null);
            } else {
                messageParams = Collections.singletonList(String.valueOf(id));
                sendMessage(context, host, Constants.APPEND_SUCCESS, messageParams);
            }
        } else {
            messageParams = Collections.singletonList(String.valueOf(lastIndex));
            sendMessage(context, host, Constants.APPEND_FAIL, messageParams);
        }
    }

    public static void commitEntryResponse(Context context, DatagramPacket request) {
        Host host = new Host(request.getAddress(), request.getPort());
        List<String> messageParams;
        sendMessage(context, host, Constants.COMMIT_SUCCESS, null);
    }

    public static void inconsistentLog(Context context, DatagramPacket request) {
        Host host = new Host(request.getAddress(), request.getPort());
        List<String> messageParams = Arrays.asList(String.valueOf(context.getStorageIndex()));
        sendMessage(context, host, Constants.INCONSISTENT_LOG, messageParams);
    }

    public static void updateInconsistentLog(Context context, DatagramPacket request, Entry entry) {
        Host host = new Host(request.getAddress(), request.getPort());
        List<String> messageParams = Arrays.asList(entry.indexStr(), entry.termStr(), entry.idStr(),
                entry.valueStr(), entry.commitedStr(), Constants.UPDATE);
        sendMessage(context, host, Constants.APPEND, messageParams);
    }


    // CLIENT MESSAGES --------------------------------------------------------------------------

    public static void rejectSetMessage(Context context, DatagramPacket request) {
        List<String> messageParams = Arrays.asList("Connect to: ", context.getLeader().getAddress().toString(),
                String.valueOf(context.getLeader().getPort()));
        Host host = new Host(request.getAddress(), request.getPort());
        sendMessage(context, host, Constants.NOT_LEADER, messageParams);
    }

    public static void acceptSetMessage(Context context, DatagramPacket request, Entry entry) {
        List<String> messageParams = Arrays.asList(entry.idStr(), entry.valueStr());
        Host host = new Host(request.getAddress(), request.getPort());
        sendMessage(context, host, Constants.SET_ACCEPTED, messageParams);
    }

    public static void sendResponseGetMessage(Context context, DatagramPacket request, Entry entry, int id) {
        List<String> messageParams;
        String messageType;

        if(entry != null) {
            messageParams = Arrays.asList(entry.idStr(), entry.valueStr());
            messageType = Constants.GET_FOUND;
        } else {
            messageParams = Arrays.asList(String.valueOf(id));
            messageType = Constants.GET_NOT_FOUND;
        }

        Host host = new Host(request.getAddress(), request.getPort());
        sendMessage(context, host, messageType, messageParams);
    }

}
