package udpServers;

import java.net.SocketException;

import context.Context;
import states.State;

public class Server {

    static State state;
    static Context context;

    public static void main(String args[]) {
        int port = Integer.parseInt(args[0]);
        if (port == 6789) {
            state = State.LEADER;
        } else {
            state = State.FOLLOWER;
        }
        try {
            context = new Context(port);
            while (state != State.HALT) {
                state = state.execute(context);
            }
        } catch (SocketException e) {
            System.out.println("Server aborted by SocketExeption");
        }
    }

}