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

    static State newtEvent() {
        return null;
    }

    static State execute(Context context) {

        System.out.println("---------- CANDIDATE ----------");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("START >>  [" + dtf.format(now) + "]");

        context.incrementTerm();
        System.out.println("Im candidate! (Term " + context.getTerm() + ")");

        voters = context.getAllHosts();

        Timer timer = new Timer();
        TimerTask postulationTask = new TimerTask() {
            @Override
            public void run() {
                Candidate.sendPostulation2(voters, context);
            }
        };
        timer.scheduleAtFixedRate(postulationTask, 0, 10000);

        //sendPostulation(context);
        Timer timeout = setTimeout(context);
        nextState = expectVotes(context);
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

    private static State expectVotes(Context context) {
        int votes = 1;
        DatagramSocket socketUDP = context.getServerSocket();
        while (votes < Constants.QUORUM) {
            byte[] buffer = new byte[1000];
            DatagramPacket acceptorResponse = new DatagramPacket(buffer, buffer.length);
            try {
                socketUDP.receive(acceptorResponse);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // entra aca si el socket fue cerrado
                e.printStackTrace();
                //return State.CANDIDATE;
                context.restartSocket();
                return State.FOLLOWER;
            }

            Gson gson = new Gson();
            String serverResponseStr = JSONUtils.parseDatagramPacket(acceptorResponse);
            Message serverResponse = gson.fromJson(serverResponseStr, Message.class);
            
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
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }
}
