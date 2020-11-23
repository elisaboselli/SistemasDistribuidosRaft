package utils;

import context.Context;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.List;

public class SendMessageUtils {

    static private void sendMessage(Context context, DatagramPacket setRequest, String messageType, List<String> messageParams) {
        try {

            Message responseMessage = new Message(0, messageType, context.getPort(), setRequest.getPort(), messageParams);
            responseMessage.log(context.getPort(), false);

            // Prepare datagram packet
            String responseMessageStr = responseMessage.toJson();
            DatagramPacket response = new DatagramPacket(responseMessageStr.getBytes(), responseMessageStr.length(),
                    setRequest.getAddress(), setRequest.getPort());

            // Send response
            context.getServerSocket().send(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendPostulation(List<Host> voters, Context context) {
        try {
            for (Host voter : voters) {
                System.out.println(">> Voter: " + voter.getPort());
            }
            for (Host voter : voters) {
                Message postulationMessage = new Message(context.getTerm(), Constants.POSTULATION, context.getPort(),
                        voter.getPort(), null);
                DatagramPacket postulationRPC = new DatagramPacket(postulationMessage.toJson().getBytes(),
                        postulationMessage.toJson().length(), voter.getAddress(), voter.getPort());
                context.getServerSocket().send(postulationRPC);
                postulationMessage.log(context.getPort(), false);
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
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
        sendMessage(context, voteRequest, messageType, messageParams);
    }

    public static void rejectSetMessage(Context context, DatagramPacket setRequest) {
        // Prepare & send response
        String messageType = Constants.NOT_LEADER;
        List<String> messageParams = Arrays.asList(messageType, context.getLeader().getAddress().toString(),
                String.valueOf(context.getLeader().getPort()));
        sendMessage(context, setRequest, messageType, messageParams);
    }

    public static void sendHeartBeat(Context context) {
        try {
            for (Host host : context.getAllHosts()) {
                Message heartBeatMessage = new Message(0, Constants.HEART_BEAT_MESSAGE, context.getPort(),
                        host.getPort(), null);
                DatagramPacket heartBeat = new DatagramPacket(heartBeatMessage.toJson().getBytes(),
                        heartBeatMessage.toJson().length(), host.getAddress(), host.getPort());
                context.getServerSocket().send(heartBeat);
                heartBeatMessage.log(context.getPort(), false);
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }
}
