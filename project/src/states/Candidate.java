package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import context.Context;
import utils.Message;
import utils.Constants;
import utils.Host;
import utils.JSONUtils;

public class Candidate {

    private static State nextState;
    private static List<Host> voters;
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    static State newtEvent() {
        return null;
    }

    static State execute(Context context) {

        System.out.println("\n-------------------------- CANDIDATE -------------------------");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("START >>  [" + dtf.format(now) + "]\n");

        context.incrementTerm();
        context.show();

        voters = context.getAllHosts();

        Timer timer = new Timer();
        TimerTask postulationTask = new TimerTask() {
            @Override
            public void run() {
                Candidate.sendPostulation2(voters, context);
            }
        };
        timer.scheduleAtFixedRate(postulationTask, 0, 5000);

        //sendPostulation(context);
        Timer timeout = setTimeout(context);
        nextState = expectVotes(context, now);
        timeout.cancel();
        postulationTask.cancel();
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

    private static Timer setTimeout(Context context) {
        Timer timer;
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                context.getServerSocket().close();
            }
        };
        //
        timer.schedule(timerTask, context.getTimeout());
        return timer;
    }

    private static State expectVotes(Context context, LocalDateTime now) {
        int votes = 1;
        DatagramSocket socketUDP = context.getServerSocket();
        while (votes < Constants.QUORUM) {
            byte[] buffer = new byte[1000];
            DatagramPacket acceptorResponse = new DatagramPacket(buffer, buffer.length);
            try {
                socketUDP.receive(acceptorResponse);
            } catch (IOException e) {
                // e.printStackTrace();
                now = LocalDateTime.now();
                System.out.println("timeout >>  [" + dtf.format(now) + "]");
                context.restartSocket();
                return State.CANDIDATE;
            }

            Gson gson = new Gson();
            String serverResponseStr = JSONUtils.parseDatagramPacket(acceptorResponse);
            Message serverResponse = gson.fromJson(serverResponseStr, Message.class);

            serverResponse.log(context.getPort(), true);
            
            switch (serverResponse.getType()) {

            case Constants.VOTE_OK:
                voters = voters.stream().filter(h -> h.getPort() != serverResponse.getFrom())
                        .collect(Collectors.toList());
                votes++;
                break;

            case Constants.HEART_BEAT_MESSAGE:
                if (serverResponse.getTerm() >= context.getTerm()) {
                    context.setTerm(serverResponse.getTerm());
                    return State.FOLLOWER;
                }
                break;

            default:
                // TODO
            }
        }
        return State.LEADER;
    }

    public static void sendPostulation2(List<Host> voters, Context context) {
        try {
            for (Host voter : voters) {
                Message postulationMessage = new Message(context.getTerm(), Constants.POSTULATION, context.getPort(),
                        voter.getPort(), null);
                DatagramPacket postulationRPC = new DatagramPacket(postulationMessage.toJson().getBytes(),
                        postulationMessage.toJson().length(), voter.getAddress(), voter.getPort());
                context.getServerSocket().send(postulationRPC);
                postulationMessage.log(context.getPort(), false);
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }
}
