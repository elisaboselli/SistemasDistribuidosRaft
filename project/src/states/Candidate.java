package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

import context.Context;
import utils.Message;
import utils.Constants;
import utils.Host;
import utils.JSONUtils;

public class Candidate {

    static State newtEvent() {
        return null;
    }

    static State execute(Context context) {
        context.incrementTerm();
        sendPostulation(context);
        Timer timeout = setTimeout(context);
        State nextState = expectVotes(context);
        timeout.cancel();
        return nextState;
    }

    public static void sendPostulation(Context context) {
        try {
            for (Host host : context.getAllHosts()) {
                Message postulationMessage = new Message(context.getTerm(), Constants.POSTULATION, context.getPort(), host.getPort(), null);
                DatagramPacket postulationRPC = new DatagramPacket(postulationMessage.toJson().getBytes(),
                        postulationMessage.toJson().length(), host.getAddress(), host.getPort());
                context.getServerSocket().send(postulationRPC);
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }

    public static Timer setTimeout(Context context) {
        Timer timer;
        timer = new Timer();
        Random random = new Random();
        random.ints(1, Constants.MIN_TIMEOUT, Constants.MAX_TIMEOUT).findFirst().getAsInt();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                context.getServerSocket().close();
                ;
            }
        };
        //
        timer.schedule(timerTask,
                random.longs(1, Constants.MIN_TIMEOUT, Constants.MAX_TIMEOUT).findFirst().getAsLong());
        return timer;
    }

    public static State expectVotes(Context context) {
        int votes = 1;
        DatagramSocket socketUDP = context.getServerSocket();
        while (votes > Constants.SERVERS_QTY / 2) {
            byte[] buffer = new byte[1000];
            DatagramPacket acceptorResponse = new DatagramPacket(buffer, buffer.length);
            try {
                socketUDP.receive(acceptorResponse);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // entra aca si el socket fue cerrado
                e.printStackTrace();
                return State.CANDIDATE;
            }

            Gson gson = new Gson();
            String serverResponseStr = JSONUtils.parseDatagramPacket(acceptorResponse);
            Message serverResponse = gson.fromJson(serverResponseStr, Message.class);
            
            switch (serverResponse.getType()) {
            case Constants.VOTE_OK:
                votes++;
                break;
            case Constants.HEART_BEAT_MESSAGE:
                // es necesario??? esta bien hacerlo??
                if (serverResponse.getTerm() >= context.getTerm()) {
                    context.setTerm(serverResponse.getTerm());
                    // context.setLeader(serverResponse.getFrom());
                    return State.FOLLOWER;
                }
                // else???
                break;
            default:
                // TODO
            }
        }
        return State.LEADER;
    }
}
