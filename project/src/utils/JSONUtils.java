package utils;

import com.google.gson.Gson;
import messages.ClientMessage;
import messages.Message;
import messages.ServerRPC;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.List;

//import messages.ClientMessage;
//import messages.ServerMessage;

public final class JSONUtils {

    private JSONUtils() {
    }

    public static List<Host> readHostFile(String filename) {
        String jsonHosts = readJSONFile(filename);
        return Host.fromJSONArray(jsonHosts);
    }

    public static String readJSONFile(String fileName) {
        String jsonStr = "";
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                jsonStr = jsonStr.concat(line);
            }
            bufferedReader.close();

        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return jsonStr;
    }

    private static String processRequest(DatagramPacket request) {
        byte[] data = new byte[request.getLength()];
        System.arraycopy(request.getData(), request.getOffset(), data, 0, request.getLength());
        return new String(data);
    }

    public static ClientMessage getClientMessage(DatagramPacket request) {
        String jsonStr = processRequest(request);
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, ClientMessage.class);
    }

    public static Message getMessage(DatagramPacket request) {
        String jsonStr = processRequest(request);
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Message.class);
    }

    public static ServerRPC getServerRPC(DatagramPacket request) {
        String jsonStr = processRequest(request);
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, ServerRPC.class);
    }

}
