package context;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.stream.Collectors;

import utils.Constants;
import utils.Host;
import utils.JSONUtils;

public class Context {
    private final static int WAIT_TIME = 60000;
    private int port;
    private DatagramSocket serverSocket;
    // private byte[] buffer;
    private List<Host> allHosts;
    private Host leader;
    private int term;

    public Context(int port, DatagramSocket serverSocket) {
        this.term = 0;
        this.serverSocket = serverSocket;
        this.port = port;
        this.leader = null;
        this.allHosts = ObtainAllHosts();
        try {
            this.serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private List<Host> ObtainAllHosts() {
        List<Host> hosts = JSONUtils.readHostFile(Constants.ALL_SERVERS_FILE);
        return hosts.stream().filter(host -> host.getPort() != this.port).collect(Collectors.toList());
    }

    public void broadcastMessage(DatagramPacket message) {
        try {
            for (Host host : this.allHosts) {
                DatagramPacket acceptorsRequest = new DatagramPacket(message.getData(), message.getLength(),
                        host.getAddress(), host.getPort());
                serverSocket.send(acceptorsRequest);
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }

    public int incrementTerm() {
        return term++;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(DatagramSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public List<Host> getAllHosts() {
        return allHosts;
    }

    public void setAllHosts(List<Host> allHosts) {
        this.allHosts = allHosts;
    }

    public Host getLeader() {
        return leader;
    }

    public void setLeader(Host leader) {
        this.leader = leader;
    }

    public static int getWaitTime() {
        return WAIT_TIME;
    }
}
