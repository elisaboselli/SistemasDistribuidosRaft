package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

import context.Context;
import messages.Message;
import utils.Constants;
import utils.JSONUtils;

public class Follower {

    private static Timer timer;
    private static int timeout;

    static class StopFollowingTask extends TimerTask {
        public void run() {
            stopFollowing();
        }
    }

    static State execute(Context context) {

        DatagramSocket socket = context.getServerSocket();
        Random random = new Random();

        // Set timeout
        timeout = random.ints(1, Constants.MIN_TIMEOUT, Constants.MAX_TIMEOUT).findFirst().getAsInt();
        timer = new Timer();
        timer.schedule(new StopFollowingTask(), timeout);

        // Wait for messages
        while (true) {
            byte[] buffer = new byte[1000];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(request);
                processMessage(context, request);
            } catch (SocketTimeoutException e) {
                return State.CANDIDATE;
            } catch (IOException e) {
                e.printStackTrace();
                return State.FOLLOWER;
            }
        }
    }

    private static void processMessage(Context context, DatagramPacket request) {
        Gson gson = new Gson();
        String serverRequestStr = JSONUtils.parseDatagramPacket(request);
        Message serverRequest = gson.fromJson(serverRequestStr, Message.class);

        switch (serverRequest.getType()) {

            // Process postulation message
            case Constants.POSTULATION:
                sendVote(context, request, serverRequest.getTerm());
                break;

            // Process heartbeat message
            case Constants.HEART_BEAT_MESSAGE:
                restarTimeout();
                break;

            // Process client message
            case Constants.CLIENT_MESSAGE:
                // Pasar el mensaje al lider
                break;

            // TODO: Other messages
            default:
                // Process message
        }
    }

    private static void restarTimeout() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new StopFollowingTask(), timeout);
    }

    private static State stopFollowing() {
        return State.CANDIDATE;
    }

    static private void sendVote(Context context, DatagramPacket voteRequest, int requestTerm) {
        try {
            // Update term if needed
            boolean isPositiveVote = context.getTerm() < requestTerm;
            if (isPositiveVote) {
                context.setTerm(requestTerm);
            }

            // Prepare response
            String messageType = isPositiveVote ? Constants.VOTE_OK : Constants.VOTE_REJECT;
            List<String> messageParams = Arrays.asList(messageType + " for term " + 1);
            Message responseMessage = new Message(0, messageType, context.getPort(), voteRequest.getPort(),
                    messageParams);

            // Prepare datagram packet
            String responseMessageStr = responseMessage.toJson();
            DatagramPacket response = new DatagramPacket(responseMessageStr.getBytes(), responseMessageStr.length(),
                    voteRequest.getAddress(), voteRequest.getPort());

            // Send response
            context.getServerSocket().send(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
