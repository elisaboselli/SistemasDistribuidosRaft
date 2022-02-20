package context;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utils.Constants;
import utils.Host;
import utils.JSONUtils;

public class Context {
    private final int port;
    private String address;
    private DatagramSocket serverSocket;
    private final int timeout;
    private List<Host> allHosts;
    private Host leader;
    private int term;
    private int storageIndex;
    private final String storageFile;
    private final String logFile;


    public Context(int port, String storageFile, String logFile, Boolean isTest) throws SocketException {
        this.term = 0;
        this.port = port;
        this.leader = null;
        this.allHosts = obtainAllHosts(isTest);
        this.storageFile = storageFile;
        this.logFile = logFile;
        this.storageIndex = 0;
        try {
            this.address = InetAddress.getLocalHost().getHostAddress();
            this.serverSocket = new DatagramSocket(port);
        } catch (SocketException | UnknownHostException e) {
            System.out.println("Socket exception: " + e.getMessage());
        }

        switch (port) {
            case 6790:
                this.timeout = 90000; // 90 seconds
                break;
            case 6789:
                this.timeout = 40000; // 40 seconds
                break;
            case 6788:
                this.timeout = 30000; // 30 seconds
                break;
            case 6787:
                this.timeout = 10000; // 10 seconds
                break;
            default:
                this.timeout = 60000; // 1 minute
        }
    }

    private List<Host> obtainAllHosts(Boolean isTest) {
        if(isTest){
            return new ArrayList<Host>();
        }

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

    public String getAddress() {
        return address;
    }

    public DatagramSocket getServerSocket() {
        return serverSocket;
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

    public int getStorageIndex() { return this.storageIndex; }

    public void updateLogIndex() { this.storageIndex++; }

    public void setStorageIndex(int index) { this.storageIndex = index; }

    public String getStorageName() {
        return this.storageFile;
    }

    public String getLogName() {
        return this.logFile;
    }

    public void restartSocket() {
        try {
            this.serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Socket exception: " + e.getMessage());
        }
    }

    public void show(String role) {
        System.out.println("--------------------------- CONTEXT UPDATED --------------------------");
        System.out.println("Role: " + role);
        System.out.println("Address: " + this.address);
        System.out.println("Port: " + this.port);
        System.out.println("Leader: " + this.leader);
        System.out.println("Term: " + this.term);
        System.out.println("Time out: " + this.timeout);

        List<String> updatedContext = new ArrayList<>();
        updatedContext.add("--------------------------- CONTEXT UPDATED --------------------------");
        updatedContext.add("Role: " + role);
        updatedContext.add("Address: " + this.address);
        updatedContext.add("Port: " + this.port);
        updatedContext.add("Leader: " + this.leader);
        updatedContext.add("Term: " + this.term);
        updatedContext.add("Time out: " + this.timeout);
        JSONUtils.writeLogFile(this.logFile, updatedContext);
    }
}
