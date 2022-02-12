package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import context.Context;
import utils.*;

public class Candidate {

    private static State nextState;
    private static List<Host> voters;
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    static State execute(Context context) {

        System.out.println("\n-------------------------- CANDIDATE -------------------------");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("START >>  [" + dtf.format(now) + "]\n");

        context.incrementTerm();
        context.setLeader(null);
        context.show(Constants.CANDIDATE);

        voters = context.getAllHosts();

        Timer timer = new Timer();
        TimerTask postulationTask = new TimerTask() {
            @Override
            public void run() {
                SendMessageUtils.sendPostulation(voters, context);
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
                now = LocalDateTime.now();
                System.out.println("timeout >>  [" + dtf.format(now) + "]");
                context.restartSocket();
                return State.CANDIDATE;
            }

            Gson gson = new Gson();
            Message serverResponse = JSONUtils.messageFromJson(acceptorResponse);
            serverResponse.log(context.getPort(), true, context.getLogName());
            List<String> params = serverResponse.getParams();

            switch (serverResponse.getType()) {

            case Constants.VOTE_OK:
                voters = voters.stream().filter(h -> h.getPort() != serverResponse.getPortFrom())
                        .collect(Collectors.toList());
                votes++;
                break;

            case Constants.HEART_BEAT_MESSAGE:
                int leaderTerm = Integer.parseInt(params.get(0));

                if (leaderTerm >= context.getTerm()) {
                    Host leaderHost = new Host(acceptorResponse.getAddress(), acceptorResponse.getPort());
                    context.setLeader(leaderHost);
                    context.setTerm(leaderTerm);
                    return State.FOLLOWER;
                }
                break;

            case Constants.POSTULATION:
                SendMessageUtils.sendVote(context, acceptorResponse, serverResponse.getTerm());
                return State.FOLLOWER;

            default:
                // TODO
            }
        }
        return State.LEADER;
    }

}
