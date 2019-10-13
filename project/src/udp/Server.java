package udp;

import context.Context;
import states.State;

public class Server {

    static State state;
    static Context context;

    public static void main(String args[]) {
        state = State.FOLLOWER;
        int port = Integer.parseInt(args[0]);
        try {
            context = new Context(port);
            while (state != State.HALT) {
                state = state.execute(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}