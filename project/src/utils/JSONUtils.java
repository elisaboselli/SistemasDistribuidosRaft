package utils;

import java.io.*;
import java.net.DatagramPacket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;

public final class JSONUtils {

    private JSONUtils() {
    }

    public static List<Host> readHostFile(String filename) {
        String jsonHosts = readJSONFile(Constants.FILES_PATH + filename);
        return Host.fromJSONArray(jsonHosts);
    }

    public static List<Log> readLogFile(String fileName) {
        String jsonLogs = readJSONFile(Constants.FILES_PATH + Constants.LOGS_PATH + fileName);
        return Log.fromJSONArray(jsonLogs);
    }

    public static void writeLogFile(String fileName, String newLog) {
        try {
            FileWriter file = new FileWriter(Constants.FILES_PATH + Constants.LOGS_PATH + fileName, true);
            BufferedWriter bufferedWriter = new BufferedWriter(file);
            bufferedWriter.write(newLog);
            bufferedWriter.newLine();
            bufferedWriter.close();

        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
            ex.printStackTrace();

        } catch (IOException e) {
            System.out.println("Error writing file '" + fileName + "'");
        }
    }

    private static String readJSONFile(String fileName) {
        String jsonStr = "";
        String line;

        try {

            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                jsonStr = jsonStr.concat(line + ",");
            }
            bufferedReader.close();

            jsonStr = "[" + jsonStr.substring(0, jsonStr.length()-1) + "]";

        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return jsonStr;
    }

    public static Message messageFromJson(DatagramPacket datagramPacket) {
        Gson gson = new Gson();
        String messageStr = parseDatagramPacket(datagramPacket);
        return gson.fromJson(messageStr, Message.class);
    }
  
    public static String parseDatagramPacket(DatagramPacket request) {
        byte[] data = new byte[request.getLength()];
        System.arraycopy(request.getData(), request.getOffset(), data, 0, request.getLength());
        return new String(data);
    }

    public static String getFileName(String fileName) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYYMMddHHmm");
        LocalDateTime now = LocalDateTime.now();
        String datePrefix = dtf.format(now) + "-";
        return datePrefix + fileName + ".txt";
    }

    public static File createLogFile(String fileName) {

        File file = new File(Constants.FILES_PATH + Constants.LOGS_PATH + getFileName(fileName));
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

}
