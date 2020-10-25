package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import context.Context;
import utils.Host;
import utils.Message;
import utils.Constants;

import static utils.JSONUtils.parseDatagramPacket;

public class Follower {

    private static int timeout = 10000;

    static State execute(Context context) {
        System.out.println("---------- FOLLOWER ----------");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("START >>  [" + dtf.format(now) + "]");

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
        Gson gson = new Gson();
        String serverRequestStr = parseDatagramPacket(request);
        Message serverRequest = gson.fromJson(serverRequestStr, Message.class);

        // Log Received Message
        String requestMessageStr = parseDatagramPacket(request);
        Message requestMessage = gson.fromJson(requestMessageStr, Message.class);
        requestMessage.log(context.getPort(), true);

        switch (serverRequest.getType()) {

            // Process postulation message
            case Constants.POSTULATION:
                sendVote(context, request, serverRequest.getTerm());
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
                rejectSetMessage(context, request);
                break;

            case Constants.CLIENT_GET_MESSAGE:
                // TODO: Acá debería responder ?
                break;

            // TODO: Other messages
            default:
                //Process message
        }
    }

    static private void sendVote(Context context, DatagramPacket voteRequest, int requestTerm) {
        // TODO: Check requestTerm >= contextTerm && requestIndex >= contextIndex

        // Update term if needed
        boolean isPositiveVote = context.getTerm() < requestTerm;
        if (isPositiveVote) {
            context.setTerm(requestTerm);
            System.out.println("New term >> " + context.getTerm());
        }

        // Prepare & send response
        String messageType = isPositiveVote ? Constants.VOTE_OK : Constants.VOTE_REJECT;
        List<String> messageParams = Arrays.asList(messageType + " for term " + requestTerm);
        sendMessage(context, voteRequest, messageType, messageParams);
    }

    static private void rejectSetMessage(Context context, DatagramPacket setRequest) {
        // Prepare & send response
        String messageType = Constants.NOT_LEADER;
        List<String> messageParams = Arrays.asList(messageType, context.getLeader().getAddress().toString(),
                String.valueOf(context.getLeader().getPort()));
        sendMessage(context, setRequest, messageType, messageParams);
    }

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
}
