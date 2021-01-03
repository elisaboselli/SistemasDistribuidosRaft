import context.Context;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Constants;
import utils.Host;
import utils.SendMessageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SendMessageUtilsTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final int port = 6787;

    private File logFile;
    private Context context;

    @BeforeEach
    public void setStreams() {
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    @After
    public void restoreInitialStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }


    @Test
    void test_sendHeartbeatMessage(){
        List<Host> hosts = new ArrayList<>();

        try {
            logFile = new File(String.valueOf(port));
            context = new Context(port, logFile.getName(), true);

            hosts.add(new Host(InetAddress.getByName("localhost"), port));
            context.setAllHosts(hosts);
            context.setLogIndex(8);

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        SendMessageUtils.sendHeartBeat(context);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.HEART_BEAT_MESSAGE, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals(8, Integer.parseInt(params.get(0)));
    }

    // Test Utils ------------------------------------------------------------------

    List<String> parseMessage(String msg){
        int i = 0;
        List<String> result = new ArrayList<>();

        i = msg.indexOf("Message ",i)  + 8;
        String message = msg.substring(i, msg.indexOf(" -",i));
        result.add(message);

        i = msg.indexOf("Type: ",i)  + 6;
        String type = msg.substring(i, msg.indexOf("\n",i));
        result.add(type);

        if(message.equals("Sent")){
            i = msg.indexOf("Sent to: ",i)  + 9;
        } else {
            i = msg.indexOf("Received from: ", i) + 15;
        }
        String port = msg.substring(i, msg.indexOf(" [",i));
        result.add(port);

        i = msg.indexOf("Params: ",i)  + 8;
        String params = msg.substring(i, msg.indexOf("\n", i));
        result.add(params);

        return result;
    }
}

