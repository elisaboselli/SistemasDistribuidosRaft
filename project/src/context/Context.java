package context;

import java.io.File;
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
    //private final static int WAIT_TIME = 60000;
    private int port;
    private DatagramSocket serverSocket;
    // private byte[] buffer;
    private List<Host> allHosts;
    private Host leader;
    private int term;
    private File logFile;
    private int timeout;

    public Context(int port, File logFile) throws SocketException {
        this.term = 0;
        this.port = port;
        this.leader = null;
        this.allHosts = obtainAllHosts();
        this.logFile = logFile;
        try {
            this.serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Socket exception: " + e.getMessage());
            throw e;
        }

        switch (port) {
            case 6789:
                this.timeout = 60000; // 1 minute
                break;
            case 6788:
                this.timeout = 40000; // 40 seconds
                break;
            case 6787:
                this.timeout = 10000; // 10 seconds
                break;
        }
    }

    private List<Host> obtainAllHosts() {
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

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public static int getWaitTime() {
        return Constants.MIN_TIMEOUT;
    }

    public void restartSocket() {
        try {
            this.serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Socket exception: " + e.getMessage());
        }
    }

    public void show() {
        System.out.println("--------------------------- CONTEXT --------------------------");
        System.out.println("Port: " + this.port);
        System.out.println("Leader: " + this.leader);
        System.out.println("Term: " + this.term);
        System.out.println("Time out: " + this.timeout);
    }
}
