package raft;

import messages.HeartBeat;
import utils.*;

import javax.imageio.IIOException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.Random;
import java.util.Timer;

public class RaftNode {

    private static Timer heart_beat_timer;

    private final long timeout;
    private int port;
    private DatagramSocket datagramSocket;
    //private byte[] buffer;
    private List<Host> allHosts;

    private String status;
    private Host leader;

    public RaftNode(long _timeout) {
        this.status = Constants.FOLLOWER;
        this.timeout = _timeout;
    }

    public RaftNode(int _port, DatagramSocket socket) {
        this.status = Constants.FOLLOWER;
        this.port = _port;
        this.leader = null;

        Random random = new Random();
        this.timeout = random.longs(1, Constants.MIN_TIMEOUT, Constants.MAX_TIMEOUT).findFirst().getAsLong();

        this.datagramSocket = socket;
        //this.buffer = new byte[1000];

        getAllHosts();
    }

    private void getAllHosts() {
        List<Host> hosts = JSONUtils.readHostFile(Constants.ALL_SERVERS_FILE);
        for (Host host : hosts) {
            if (host.getPort() != this.port)
                this.allHosts.add(host);
        }
    }

    public long getTimeout() {
        return timeout;
    }

    public int getPort(){
        return this.port;
    }

    public boolean isLeader() {
        return this.status.equals(Constants.LEADER);
    }

    public void postulate(){}

    public void sendHeartBeat() {
        if (this.isLeader()) {
            try {
                for (Host host : this.allHosts) {
                    HeartBeat heartBeatMessage = new HeartBeat(this.port, host.getPort());
                    DatagramPacket heartBeat = new DatagramPacket(heartBeatMessage.toJSON().getBytes(),
                            heartBeatMessage.toJSON().length(), host.getAddress(), host.getPort());
                    datagramSocket.send(heartBeat);
                }
            } catch (IOException e) {
                System.out.println("IO Exception: " + e.getMessage());
            }
        }
    }

    public void broadcastMessage(DatagramPacket message) {
        try {
            for (Host host : this.allHosts) {
                DatagramPacket acceptorsRequest = new DatagramPacket(message.getData(), message.getLength(), host.getAddress(), host.getPort());
                datagramSocket.send(acceptorsRequest);
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }
}
