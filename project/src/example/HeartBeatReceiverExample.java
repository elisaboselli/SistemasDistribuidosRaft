package example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

import utils.Message;
import utils.Constants;

public class HeartBeatReceiverExample {

    final static int WAIT_TIME = 30000;
    private static Timer wait_timer;

    public static void main(String args[]) {

        int heartBeatCount = 0;
       
        Boolean stillFollower = true;
        class StopFollowingTask extends TimerTask{
            public void run() {
                stopFollowing(stillFollower);
            }
        }

        wait_timer = new Timer();
        wait_timer.schedule(new StopFollowingTask(), WAIT_TIME);

        try {

            System.out.println("\n---------- BEGIN FOLLOWER SERVER ----------\n");

            // Open UDP Socket
            int localPort = 6789;
            DatagramSocket socketUDP = new DatagramSocket(localPort);

            //while(heartBeatCount < 5){
            while(stillFollower){

                wait_timer.cancel();
                wait_timer = new Timer();
                wait_timer.schedule(new StopFollowingTask(), WAIT_TIME);
                System.out.println("restart timer");
                System.out.println("\n\n--------------------------------------------------------------\n");

                // Receive request
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(request);

                // Parse request
                Gson gson = new Gson();
                String requestMessageStr = parseDatagramPacket(request);
                Message heartBeatMessage = gson.fromJson(requestMessageStr, Message.class);
                heartBeatMessage.log(localPort, true);

                // Prepare response
                List<String> messageParams = Arrays.asList("HeartBeat ok");
                Message responseMessage = new Message(0, Constants.EMPTY_MESSAGE, localPort,
                    request.getPort(),messageParams);

                // Prepare datagram packet
                String responseMessageStr = responseMessage.toJson();
                DatagramPacket response = new DatagramPacket(responseMessageStr.getBytes(), responseMessageStr.length(),
                    request.getAddress(), request.getPort());

                // Send response
                socketUDP.send(response);
                heartBeatCount++;
            }

            socketUDP.close();
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

    private static void stopFollowing(Boolean following) {
        following = false;
        System.out.println("stop following");
    }
}
