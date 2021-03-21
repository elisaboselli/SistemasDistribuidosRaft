package udpServers;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import utils.Message;
import utils.Constants;
import utils.JSONUtils;

public class Client {

    public static void main(String args[]) {

        try {

            // Initialize ports
            int localPort = 6790;
            int serverPort = Integer.parseInt(args[0]);

            // Create UDP Socket
            DatagramSocket socketUDP = new DatagramSocket(localPort);
            InetAddress hostServer = InetAddress.getByName("localhost");
            File logFile = JSONUtils.createLogFile(String.valueOf(localPort), false);

            // Initialize scanner
            Scanner scan = new Scanner(System.in);
            boolean nextOp = true;

            System.out.println("\n---------- BEGIN CLIENT ----------\n");

            while (nextOp) {

                // Get client operation
                String operation = getOperationFromInput(scan);

                List<String> params = new ArrayList<String>();
                String messageType = Constants.EMPTY_MESSAGE;
                int id, value;

                if (operation.equalsIgnoreCase(Constants.GET)) {
                    id = getIdFromInput(scan);
                    params.add(String.valueOf(id));
                    messageType = Constants.GET;
                }

                if (operation.equalsIgnoreCase(Constants.SET)) {
                    id = getIdFromInput(scan);
                    value = getValueFromInput(scan);
                    params.add(String.valueOf(id));
                    params.add(String.valueOf(value));
                    messageType = Constants.SET;
                }

                // Prepare request message
                Message requestMessage = new Message(Constants.NO_TERM, messageType, localPort, serverPort, params);
                requestMessage.log(localPort, false, logFile.getName());

                // Prepare datagram package
                String requestMessageStr = requestMessage.toJson();
                DatagramPacket request = new DatagramPacket(requestMessageStr.getBytes(), requestMessageStr.length(),
                        hostServer, serverPort);

                // Send message
                socketUDP.send(request);

                // Receive response
                byte[] buffer = new byte[1000];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(response);

                // Parse Response
                Message responseMessage = JSONUtils.messageFromJson(response);
                responseMessage.log(localPort, true, logFile.getName());

                // Prepare for next operation
                nextOp = getNextOperationFromInput(scan);

            }

            // Close scanner & UDP Socket
            scan.close();
            socketUDP.close();

            System.out.println("\n---------- CLOSING CLIENT SERVER ----------\n");

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }

    private static String getOperationFromInput(Scanner scan) {
        String input = "";
        boolean inputCorrect = false;

        System.out.println("Enter the operation to be performed (GET / SET)");
        while (!inputCorrect) {
            input = scan.nextLine();
            inputCorrect = (input.equalsIgnoreCase("get") || input.equalsIgnoreCase("set"));
            if (!inputCorrect) {
                System.out.println("Please, enter a valid operation (GET / SET)");
            }
        }
        return input;
    }

    private static boolean getNextOperationFromInput(Scanner scan) {

        String input;
        scan.nextLine();
        System.out.println("Do you want to perform another operation ? (Y / N)");
        while (true) {
            input = scan.nextLine();
            if (input.equalsIgnoreCase("Y")) {
                return true;
            } else {
                if (input.equalsIgnoreCase("N")) {
                    return false;
                }
            }
            System.out.println("Please, enter a valid option (Y / N)");
        }
    }

    private static int getIdFromInput(Scanner scan) {
        System.out.println("Enter the element id");
        return scan.nextInt();
    }

    private static int getValueFromInput(Scanner scan) {
        System.out.println("Enter the element new value");
        return scan.nextInt();
    }
}
