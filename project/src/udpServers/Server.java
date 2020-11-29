package udpServers;

import java.io.File;
import java.net.SocketException;

import context.Context;
import states.State;
import utils.JSONUtils;

public class Server {

    static State state;
    static Context context;

    public static void main(String args[]) {
        int port = Integer.parseInt(args[0]);
        state = State.FOLLOWER;
        /*if (port == 6789) {
            state = State.LEADER;
        } else {
            state = State.FOLLOWER;
        }*/

        File logFile = JSONUtils.createLogFile(String.valueOf(port));
        try {
            context = new Context(port, logFile, false);
            context.show();
            while (state != State.HALT) {
                state = state.execute(context);
            }
            throw new SocketException();
        } catch (SocketException e) {
            System.out.println("Server aborted by SocketExeption");
        }
    }

}