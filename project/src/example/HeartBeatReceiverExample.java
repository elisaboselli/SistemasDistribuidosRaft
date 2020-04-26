package example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.google.gson.Gson;

import messages.HeartBeat;
import utils.Constants;

public class HeartBeatReceiverExample {
    public static void main(String args[]) {

        int heartBeatCount = 0;

        try {

            System.out.println("\n---------- BEGIN SERVER ----------\n");

            // Open UDP Socket
            int localPort = 6789;
            DatagramSocket socketUDP = new DatagramSocket(localPort);

            while(heartBeatCount < 5){
                // Receive request
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(request);

                // Parse request
                Gson gson = new Gson();
                String requestMessageStr = parseDatagramPacket(request);
                HeartBeat heartBeatMessage = gson.fromJson(requestMessageStr, HeartBeat.class);
                heartBeatMessage.log(localPort);

                // Prepare response
                MessageExample responseMessage = new MessageExample(0, Constants.SERVER_MESSAGE, localPort,
                    request.getPort(), "Heartbeat ok");

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
}
