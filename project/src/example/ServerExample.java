package example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.google.gson.Gson;

import utils.Constants;

public class ServerExample {
    public static void main(String args[]) {

        try {

            System.out.println("\n---------- BEGIN SERVER ----------\n");

            // Open UDP Socket
            int localPort = Integer.parseInt(args[0]);
            DatagramSocket socketUDP = new DatagramSocket(localPort);

            // Receive request
            byte[] buffer = new byte[1000];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socketUDP.receive(request);

            // Parse request
            Gson gson = new Gson();
            String requestMessageStr = parseDatagramPacket(request);
            MessageExample requestMessage = gson.fromJson(requestMessageStr, MessageExample.class);
            requestMessage.log(localPort);

            // Prepare response
            MessageExample responseMessage = new MessageExample(0, Constants.SERVER_MESSAGE, localPort,
                    request.getPort(), "Hello from the other side");
            responseMessage.log(localPort);

            // Prepare datagram packet
            String responseMessageStr = responseMessage.toJson();
            DatagramPacket response = new DatagramPacket(responseMessageStr.getBytes(), responseMessageStr.length(),
                    request.getAddress(), request.getPort());

            // Send response
            socketUDP.send(response);
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
