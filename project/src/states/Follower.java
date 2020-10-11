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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

import context.Context;
import utils.Message;
import utils.Constants;
import utils.JSONUtils;

public class Follower {

    private static Timer timer;
    private static int timeout;

    static State execute(Context context) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("START >>  [" + dtf.format(now) + "]");

        DatagramSocket socket = context.getServerSocket();
        try {
            socket.setSoTimeout(10000);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Set timeout
        // Random random = new Random();
        // timeout = random.ints(1, Constants.MIN_TIMEOUT,
        // Constants.MAX_TIMEOUT).findFirst().getAsInt();

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
        String serverRequestStr = JSONUtils.parseDatagramPacket(request);
        Message serverRequest = gson.fromJson(serverRequestStr, Message.class);

        switch (serverRequest.getType()) {

            // Process postulation message
            case Constants.POSTULATION:
                sendVote(context, request, serverRequest.getTerm());
                break;

            // Process heartbeat message
            case Constants.HEART_BEAT_MESSAGE:
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println("Hearbeat message received from " + request.getPort()
                        + " [" + dtf.format(now) + "]");
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

    static private void sendVote(Context context, DatagramPacket voteRequest, int requestTerm) {
        try {
            // Update term if needed
            boolean isPositiveVote = context.getTerm() < requestTerm;
            if (isPositiveVote) {
                context.setTerm(requestTerm);
            }

            // Prepare response
            String messageType = isPositiveVote ? Constants.VOTE_OK : Constants.VOTE_REJECT;
            List<String> messageParams = Arrays.asList(messageType + " for term " + requestTerm);
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
