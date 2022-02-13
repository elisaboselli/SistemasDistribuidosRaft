package udpServers;

import java.io.File;
import java.net.SocketException;

import context.Context;
import states.State;
import utils.Constants;
import utils.JSONUtils;

public class Server {

    private static State state;
    private static Context context;

    public static void main(String args[]) {
        int port = Integer.parseInt(args[0]);
        state = State.FOLLOWER;

        File storageFile = JSONUtils.createStorageFile(String.valueOf(port));
        File logFile = JSONUtils.createLogFile(String.valueOf(port), true);
        try {
            context = new Context(port, storageFile.getName(), logFile.getName(), false);
            while (state != State.HALT) {
                state = state.execute(context);
            }
            throw new SocketException();
        } catch (SocketException e) {
            System.out.println("Server aborted by SocketExeption");
        }
    }

}