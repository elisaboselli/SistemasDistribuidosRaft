import context.Context;
import org.junit.jupiter.api.Test;
import states.State;
import utils.Constants;
import utils.Message;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

class FSMTest {

    @Test
    void followerToCandidate() {
        int port = 6787;
        State state = State.FOLLOWER;
        State newState = null;
        Context context = null;
        int originalTerm = 0;

        File logFile = new File(String.valueOf(port));

        try {
            context = new Context(port, logFile, true);
            originalTerm = context.getTerm();
            newState = state.execute(context);
            context.getServerSocket().close();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // After timeout
        assert newState == State.CANDIDATE;
        assert originalTerm == context.getTerm();

    }

    @Test
    void candidateToCandidate() {
        int port = 6787;
        State state = State.CANDIDATE;
        State newState = null;
        Context context = null;
        int originalTerm = 0;

        File logFile = new File(String.valueOf(port));

        try {
            context = new Context(port, logFile, true);
            originalTerm = context.getTerm();
            newState = state.execute(context);
            context.getServerSocket().close();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // After timeout with no votes
        assert newState == State.CANDIDATE;
        assert originalTerm + 1 == context.getTerm();
    }

    @Test
    void candidateToLeader() {
        int port = 6787;
        State state = State.CANDIDATE;
        State newState = null;
        Context context = null;
        int originalTerm = 0;

        File logFile = new File(String.valueOf(port));

        Thread thread = new Thread("SendVote") {
            public void run(){
                try {
                    // Open UDP Socket
                    DatagramSocket socketUDP = new DatagramSocket(1234);
                    InetAddress hostServer = InetAddress.getByName("localhost");

                    // Prepare menssage
                    List<String> messageParams = Arrays.asList(Constants.VOTE_OK + " for term " + 4);
                    Message responseMessage = new Message(0, Constants.VOTE_OK, 1234, port,
                            messageParams);

                    // Prepare datagram packet
                    String requestMessageStr = responseMessage.toJson();
                    DatagramPacket request = new DatagramPacket(requestMessageStr.getBytes(), requestMessageStr.length(),
                            hostServer, port);

                    // Send message
                    socketUDP.send(request);

                    // Close UDP Socket
                    socketUDP.close();

                } catch (SocketException e) {
                    System.out.println("Socket: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO: " + e.getMessage());
                }
            }
        };
        thread.start();

        try {
            context = new Context(port, logFile, true);
            originalTerm = context.getTerm();
            newState = state.execute(context);
            context.getServerSocket().close();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        // After timeout with no votes
        assert newState == State.LEADER;
        assert originalTerm + 1 == context.getTerm();
    }
}
