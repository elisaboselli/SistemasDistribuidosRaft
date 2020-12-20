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
            context = new Context(port, logFile.getName(), true);
            originalTerm = context.getTerm();
            newState = state.execute(context);
            context.getServerSocket().close();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // After timeout
        assert newState == State.CANDIDATE;
        assert context.getLeader() == null;
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
            context = new Context(port, logFile.getName(), true);
            originalTerm = context.getTerm();
            newState = state.execute(context);
            context.getServerSocket().close();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // After timeout with no votes
        assert newState == State.CANDIDATE;
        assert context.getLeader() == null;
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
                    DatagramPacket request = new DatagramPacket(requestMessageStr.getBytes(), requestMessageStr.length(), hostServer, port);

                    // Send message
                    Thread.sleep(2000);
                    socketUDP.send(request);

                    // Close UDP Socket
                    socketUDP.close();

                } catch (SocketException e) {
                    System.out.println("Socket: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO: " + e.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            context = new Context(port, logFile.getName(), true);
            originalTerm = context.getTerm();
            newState = state.execute(context);
            context.getServerSocket().close();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        // After timeout with no votes
        assert newState == State.LEADER;
        assert context.getLeader() == null;
        assert originalTerm + 1 == context.getTerm();
    }


    @Test
    void candidateToFollower_heartBeat() {
        int port = 6787;
        State state = State.CANDIDATE;
        State newState = null;
        Context context = null;

        File logFile = new File(String.valueOf(port));

        Thread thread = new Thread("SendHeartBeat") {
            public void run(){
                try {
                    // Open UDP Socket
                    DatagramSocket socketUDP = new DatagramSocket(1234);
                    InetAddress hostServer = InetAddress.getByName("localhost");

                    // Prepare menssage
                    Message responseMessage = new Message(3, Constants.HEART_BEAT_MESSAGE,1234, port,null);

                    // Prepare datagram packet
                    String requestMessageStr = responseMessage.toJson();
                    DatagramPacket request = new DatagramPacket(requestMessageStr.getBytes(), requestMessageStr.length(), hostServer, port);

                    // Send message
                    Thread.sleep(2000);
                    socketUDP.send(request);

                    // Close UDP Socket
                    socketUDP.close();

                } catch (SocketException e) {
                    System.out.println("Socket: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO: " + e.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            context = new Context(port, logFile.getName(), true);
            newState = state.execute(context);
            context.getServerSocket().close();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        // After timeout with no votes
        assert newState == State.FOLLOWER;
        assert context.getLeader().getPort() == 1234;
        assert context.getTerm() == 3;
    }

    @Test
    void candidateToFollower_postulation() {
        int port = 6787;
        State state = State.CANDIDATE;
        State newState = null;
        Context context = null;

        File logFile = new File(String.valueOf(port));

        Thread thread = new Thread("SendPostulation") {
            public void run(){
                try {
                    // Open UDP Socket
                    DatagramSocket socketUDP = new DatagramSocket(1234);
                    InetAddress hostServer = InetAddress.getByName("localhost");

                    // Prepare menssage
                    Message responseMessage = new Message(3, Constants.POSTULATION,1234, port,null);

                    // Prepare datagram packet
                    String requestMessageStr = responseMessage.toJson();
                    DatagramPacket request = new DatagramPacket(requestMessageStr.getBytes(), requestMessageStr.length(), hostServer, port);

                    // Send message
                    Thread.sleep(2000);
                    socketUDP.send(request);

                    // Close UDP Socket
                    socketUDP.close();

                } catch (SocketException e) {
                    System.out.println("Socket: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO: " + e.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            context = new Context(port, logFile.getName(), true);
            newState = state.execute(context);
            context.getServerSocket().close();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        // After timeout with no votes
        assert newState == State.FOLLOWER;
        assert context.getLeader().getPort() == 1234;
        assert context.getTerm() == 3;
    }

    @Test
    void leaderToFollower_heartBeat() {
        int port = 6787;
        State state = State.LEADER;
        State newState = null;
        Context context = null;

        File logFile = new File(String.valueOf(port));

        Thread thread = new Thread("SendHeartBeat") {
            public void run(){
                try {
                    // Open UDP Socket
                    DatagramSocket socketUDP = new DatagramSocket(1234);
                    InetAddress hostServer = InetAddress.getByName("localhost");

                    // Prepare menssage
                    Message responseMessage = new Message(3, Constants.HEART_BEAT_MESSAGE,1234, port,null);

                    // Prepare datagram packet
                    String requestMessageStr = responseMessage.toJson();
                    DatagramPacket request = new DatagramPacket(requestMessageStr.getBytes(), requestMessageStr.length(), hostServer, port);

                    // Send message
                    Thread.sleep(2000);
                    socketUDP.send(request);

                    // Close UDP Socket
                    socketUDP.close();

                } catch (SocketException e) {
                    System.out.println("Socket: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO: " + e.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            context = new Context(port, logFile.getName(), true);
            newState = state.execute(context);
            context.getServerSocket().close();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        // After timeout with no votes
        assert newState == State.FOLLOWER;
        assert context.getLeader().getPort() == 1234;
        assert context.getTerm() == 3;
    }

    @Test
    void leaderToFollower_postulation() {
        int port = 6787;
        State state = State.LEADER;
        State newState = null;
        Context context = null;

        File logFile = new File(String.valueOf(port));

        Thread thread = new Thread("SendPostulation") {
            public void run(){
                try {
                    // Open UDP Socket
                    DatagramSocket socketUDP = new DatagramSocket(1234);
                    InetAddress hostServer = InetAddress.getByName("localhost");

                    // Prepare menssage
                    Message responseMessage = new Message(3, Constants.POSTULATION,1234, port,null);

                    // Prepare datagram packet
                    String requestMessageStr = responseMessage.toJson();
                    DatagramPacket request = new DatagramPacket(requestMessageStr.getBytes(), requestMessageStr.length(), hostServer, port);

                    // Send message
                    Thread.sleep(2000);
                    socketUDP.send(request);

                    // Close UDP Socket
                    socketUDP.close();

                } catch (SocketException e) {
                    System.out.println("Socket: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO: " + e.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            context = new Context(port, logFile.getName(), true);
            newState = state.execute(context);
            context.getServerSocket().close();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        // After timeout with no votes
        assert newState == State.FOLLOWER;
        assert context.getLeader().getPort() == 1234;
        assert context.getTerm() == 3;
    }
}
