package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

//import messages.ClientMessage;
//import messages.ClientMessageType;
//import messages.Element;
import messages.ClientMessage;
import messages.Message;
import utils.Constants;

public class Client {

    public static void main(String args[]) {

        try {
            DatagramSocket socketUDP = new DatagramSocket();
            InetAddress hostServer = InetAddress.getByName(args[0]);
            int portServer = 6789;

            Scanner scan = new Scanner(System.in);
            boolean nextOp = true;

            System.out.println("\n---------- CLIENT SERVER ----------\n");

            while (nextOp) {
                String operation = getOperationFromInput(scan);
                int id, value;
                ClientMessage clientMessage = new ClientMessage();

                if (operation.equalsIgnoreCase(Constants.CLIENT_GET)) {
                    id = getIdFromInput(scan);
                    clientMessage.setType(Constants.CLIENT_GET);
                    clientMessage.setId(id);
                }

                if (operation.equalsIgnoreCase(Constants.CLIENT_SET)) {
                    id = getIdFromInput(scan);
                    value = getValueFromInput(scan);
                    clientMessage.setType(Constants.CLIENT_SET);
                    clientMessage.setId(id);
                    clientMessage.setValue(value);
                }

                clientMessage.setAddress(InetAddress.getLocalHost());
                clientMessage.setPort(socketUDP.getLocalPort());

                Message message = new Message(Constants.CLIENT_MESSAGE, clientMessage.toJSON());

                DatagramPacket request = new DatagramPacket(message.toJSON().getBytes(), message.toJSON().length(),
                        hostServer, portServer);

                socketUDP.send(request);

                byte[] buffer = new byte[1000];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(response);

                // Get Response
                // ClientMessage responseMsg = jsonUtils.getClientMessage(response);
                // System.out.println("Response: " + responseMsg.toString());

                nextOp = getNextOperationFromInput(scan);

            }
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
