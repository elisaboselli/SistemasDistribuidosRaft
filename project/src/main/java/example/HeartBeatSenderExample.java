package example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

import utils.Message;
import utils.Constants;

public class HeartBeatSenderExample {

    final static int WAIT_TIME = 60000;
    private static Timer heart_beat_timer;

    public static void main(String args[]) {

        int heartBeatCount = 0;

        try {

            System.out.println("\n---------- BEGIN LEADER SERVER ----------\n");

            // Open UDP Socket
            int localPort = 6790;
            DatagramSocket socketUDP = new DatagramSocket(localPort);
            int serverPort = 6789;

            // Heart Beat Sender
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    sendHeartBeat(socketUDP, localPort, serverPort);
                }
            };

            heart_beat_timer = new Timer();
            heart_beat_timer.scheduleAtFixedRate(timerTask, 0, Constants.HEART_BEAT);

            while (heartBeatCount < 2){
                // Receive request
                byte[] buffer = new byte[1000];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(response);

                // Parse request
                Gson gson = new Gson();
                String responseMessageStr = parseDatagramPacket(response);
                Message responseMsg = gson.fromJson(responseMessageStr, Message.class);
                responseMsg.log(localPort, true, "");

                heartBeatCount++;
            }

            timerTask.cancel();
            heart_beat_timer.cancel();
            
            System.out.println("\n---------- END SERVER ----------\n");

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }

    private static String parseDatagramPacket(DatagramPacket dp) {
        byte[] data = new byte[dp.getLength()];
        System.arraycopy(dp.getData(), dp.getOffset(), data, 0, dp.getLength());
        return new String(data);
    }

    private static void sendHeartBeat(DatagramSocket datagramSocket, int localPort, int remotePort) {
        try {
            List<String> params = new ArrayList<String>();
            InetAddress localhost = InetAddress.getByName("localhost");
            Message heartBeatMessage = new Message(0, Constants.HEART_BEAT_MESSAGE, localPort, remotePort,params);
            DatagramPacket heartBeat = new DatagramPacket(heartBeatMessage.toJson().getBytes(), heartBeatMessage.toJson().length(), localhost, remotePort);
            datagramSocket.send(heartBeat);
            heartBeatMessage.log(localPort, false, "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
