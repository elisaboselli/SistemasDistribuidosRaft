package udp;

import java.net.DatagramSocket;
import java.net.SocketException;

import context.Context;
import states.State;

public class Server {

    static State state;
    static Context context;

    public static void main(String args[]) {
        state = State.FOLLOWER;
        int port = Integer.parseInt(args[0]);
        DatagramSocket serverSocket;
        try {
            serverSocket = new DatagramSocket(port);
            context = new Context(port, serverSocket);
            while (state != State.HALT) {
                state = state.execute(context);
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}