package example;

import utils.Entry;
import utils.JSONUtils;
import utils.Log;

public class LogExample {

    public static void main(String args[]) {
        String fileName = JSONUtils.getFileName("prueba");
        JSONUtils.createLogFile(fileName);

        Log log = new Log();

        Entry entry1 = new Entry(1,1,1,1);
        log.appendEntry(entry1);

        String logStr = log.toJson();
        JSONUtils.writeLogFile(fileName, logStr);

        Log readedLog = JSONUtils.readLogFile(fileName);

        if(readedLog.getEntryList().size() == 1) {
            System.out.println("First Read");
            readedLog.printLog();
        }

        Entry entry2 = new Entry(1, 2, 2,2);
        log.appendEntry(entry2);

        logStr = log.toJson();
        JSONUtils.writeLogFile(fileName, logStr);

        readedLog = JSONUtils.readLogFile(fileName);
        if(readedLog.getEntryList().size() == 2) {
            System.out.println("\n\nSecond Read");
            readedLog.printLog();
        }
    }
}
