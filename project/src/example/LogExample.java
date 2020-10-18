package example;

import utils.JSONUtils;
import utils.Log;

import java.util.List;

public class LogExample {

    public static void main(String args[]) {
        String fileName = JSONUtils.getFileName("prueba");
        JSONUtils.createLogFile(fileName);

        Log log1 = new Log(1,1,1,1);
        String log1Str = log1.toJson();
        JSONUtils.writeLogFile(fileName, log1Str);

        Log log2 = new Log(1, 2, 1,1);
        String log2Str = log2.toJson();
        JSONUtils.writeLogFile(fileName, log2Str);

        List<Log> logs = JSONUtils.readLogFile(fileName);

        assert logs.size() == 2;
    }
}
