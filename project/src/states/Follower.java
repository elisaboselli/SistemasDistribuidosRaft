package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

import com.google.gson.Gson;

import context.Context;
import utils.Message;
import utils.Constants;
import utils.JSONUtils;

public class Follower {

    static State execute(Context context) {
        DatagramSocket socket = context.getServerSocket();
        Random random = new Random();
        try {
            socket.setSoTimeout(random.ints(1, Constants.MIN_TIMEOUT, Constants.MAX_TIMEOUT).findFirst().getAsInt());
            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket getMessage = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(getMessage);
                    processMessage(context, getMessage);
                } catch (SocketTimeoutException e) {
                    return State.CANDIDATE;
                } catch (IOException e) {
                    e.printStackTrace();
                    return State.FOLLOWER;
                }
            }
        } catch (SocketException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return State.FOLLOWER;
        }
        // poner el timeout en 0
    }

    private static void processMessage(Context context, DatagramPacket datagramPacket) {
        Gson gson = new Gson();
        String serverResponseStr = JSONUtils.parseDatagramPacket(datagramPacket);
        Message serverResponse = gson.fromJson(serverResponseStr, Message.class);
        
        switch (serverResponse.getType()) {
        case Constants.POSTULATION:
            if (context.getTerm() < serverResponse.getTerm()) {
                // votar a favor
                context.setTerm(serverResponse.getTerm());
            }
            break;
        case Constants.HEART_BEAT_MESSAGE:
            // Reiniciar el timeout
            break;
        case Constants.CLIENT_MESSAGE:
            // Pasar el mensaje al lider
            break;
        default:
            // Process message
        }
    }
}
