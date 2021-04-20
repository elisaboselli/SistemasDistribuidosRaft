package utils;

import com.google.gson.Gson;
import org.json.JSONArray;

import java.io.*;
import java.net.DatagramPacket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class JSONUtils {

    private JSONUtils() {
    }

    public static List<Host> readHostFile(String filename) {
        String jsonHosts = readJSONFile(Constants.FILES_PATH + filename);
        return Host.fromJSONArray(jsonHosts);
    }

    public static Storage readStorageFile(String fileName) {
        String longFileName = Constants.FILES_PATH + Constants.STORAGE_PATH + fileName;
        String line;
        String jsonStr = "";

        try {

            FileReader fileReader = new FileReader(longFileName);
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

        return Storage.fromJsonArray(jsonStr);
    }

    public static void writeStorageFile(String fileName, JSONArray updatedStorage) {
        String path = Constants.FILES_PATH + Constants.STORAGE_PATH + fileName;
        overwriteFile(path, updatedStorage);
    }

    public static void writeLogFile(String fileName, List<String> newLog) {
        String path = Constants.FILES_PATH + Constants.LOGS_PATH + fileName;
        writeFile(path, newLog);
    }

    private static void writeFile(String path, List<String> input) {
        try {
            FileWriter file = new FileWriter(path, true);
            BufferedWriter bufferedWriter = new BufferedWriter(file);
            for(String line : input) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + path + "'");
            ex.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error writing file '" + path + "'");
        }
    }

    private static void overwriteFile(String path, JSONArray input) {
        try {
            FileWriter file = new FileWriter(path, false);
            file.write(input.toString(1));
            file.flush();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + path + "'");
            ex.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error writing file '" + path + "'");
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

    public static String getFileName(String fileName, Boolean isServer, Boolean isStorage) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYYMMddHHmm");
        LocalDateTime now = LocalDateTime.now();
        String datePrefix = dtf.format(now) + "-";
        String role = (isServer ? Constants.SERVER : Constants.CLIENT) + "-";
        String ext = isStorage ? ".json" : ".txt";
        return datePrefix + role + fileName + ext;
    }

    public static File createStorageFile(String fileName) {

        File file = new File(Constants.FILES_PATH + Constants.STORAGE_PATH + getFileName(fileName, true, true));
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static File createLogFile(String fileName, Boolean isServer) {

        File file = new File(Constants.FILES_PATH + Constants.LOGS_PATH + getFileName(fileName, isServer, false));
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

}
