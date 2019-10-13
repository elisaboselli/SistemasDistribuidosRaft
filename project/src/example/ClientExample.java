package example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.google.gson.Gson;

import utils.Constants;

public class ClientExample {

    public static void main(String args[]) {

        try {

            System.out.println("\n---------- BEGIN CLIENT ----------\n");

            // Open UDP Socket
            DatagramSocket socketUDP = new DatagramSocket(6790);
            InetAddress hostServer = InetAddress.getByName("localhost");

            // Take server port from args
            int serverPort = Integer.parseInt(args[0]);
            int localPort = socketUDP.getLocalPort();

            // Prepare menssage
            MessageExample requestMessage = new MessageExample(0, Constants.CLIENT_MESSAGE, localPort, serverPort,
                    "Hello");
            requestMessage.log(localPort);

            // Prepare datagram packet
            String requestMessageStr = requestMessage.toJson();
            DatagramPacket request = new DatagramPacket(requestMessageStr.getBytes(), requestMessageStr.length(),
                    hostServer, serverPort);

            // Send message
            socketUDP.send(request);

            // Receive response
            byte[] buffer = new byte[1000];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socketUDP.receive(response);

            // Parse response
            Gson gson = new Gson();
            String responseMessageStr = parseDatagramPacket(response);
            MessageExample responseMessage = gson.fromJson(responseMessageStr, MessageExample.class);
            responseMessage.log(localPort);

            // Close UDP Socket
            socketUDP.close();
            System.out.println("\n---------- END CLIENT ----------\n");

        } catch (

        SocketException e) {
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
