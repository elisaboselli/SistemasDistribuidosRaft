import context.Context;
import org.junit.After;
import org.junit.jupiter.api.*;
import utils.Constants;
import utils.Entry;
import utils.Host;
import utils.SendMessageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SendMessageUtilsTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private static final int port = 6787;

    private static File storageFile;
    private static File logFile;
    private static List<Host> hosts;
    private static DatagramPacket datagramPacket;
    private Context context;

    @BeforeAll
    static void setContext() {
        storageFile = new File(String.valueOf(port));
        logFile = new File(String.valueOf(port));
        hosts = new ArrayList<>();
        String msg = "something";

        try {
            hosts.add(new Host(InetAddress.getByName("localhost"), port));
            datagramPacket = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("localhost"), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void setStreams() {
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));

        try {
            context = new Context(port, storageFile.getName(), logFile.getName(), true);
            context.setAllHosts(hosts);

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void closeSocket() {
        context.getServerSocket().close();
    }

    @After
    public void restoreInitialStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }


    @Test
    void test_sendHeartbeatMessage(){

        context.setStorageIndex(8);

        SendMessageUtils.sendHeartBeat(context);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.HEART_BEAT_MESSAGE, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals(8, Integer.parseInt(params.get(0)));
    }

   @Test
    void test_sendPostulationMessage(){

        context.setStorageIndex(8);

        SendMessageUtils.sendPostulation(hosts, context);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.POSTULATION, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));
    }

    @Test
    void test_sendPositiveVoteMessage(){

        context.setTerm(3);

        SendMessageUtils.sendVote(context, datagramPacket, 4);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.VOTE_OK, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals("vote_ok", params.get(0));
        assertEquals("4", params.get(3));
    }

    @Test
    void test_sendNegativeVoteMessage(){

        context.setTerm(3);

        SendMessageUtils.sendVote(context, datagramPacket, 2);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.VOTE_REJECT, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals("vote_reject", params.get(0));
        assertEquals("2", params.get(3));
    }

    @Test
    void test_sendRejectSetMessage(){

        context.setLeader(hosts.get(0));

        SendMessageUtils.rejectSetMessage(context, datagramPacket);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.NOT_LEADER, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals("localhost/127.0.0.1", params.get(2));
        assertEquals(String.valueOf(port), params.get(3));
    }

    @Test
    void test_sendAppendEntryMessage(){

        Entry entry = new Entry(1,2,3,4);

        SendMessageUtils.appendEntry(context, entry);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.APPEND, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals(entry.indexStr(), params.get(0));
        assertEquals(entry.termStr(), params.get(1));
        assertEquals(entry.idStr(), params.get(2));
        assertEquals(entry.valueStr(), params.get(3));
    }

    @Test
    void test_sendAppendEntryResponseSuccesssMessage(){

        SendMessageUtils.appendEntryResponse(context, datagramPacket,true, 1,5, false);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.APPEND_SUCCESS, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));
    }

    @Test
    void test_sendAppendEntryResponseFailureMessage(){

        SendMessageUtils.appendEntryResponse(context, datagramPacket,false, 1,5, false);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.APPEND_FAIL, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals("5", params.get(0));

    }

    @Test
    void test_sendAppendEntryResponseInconsistentLogMessage(){

        context.setStorageIndex(3);

        SendMessageUtils.appendEntryResponse(context, datagramPacket,true, 1,5, true);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.UPDATE_SUCCESS, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));
    }

    @Test
    void test_sendInconsistentLogMessage(){

        context.setStorageIndex(3);

        SendMessageUtils.inconsistentLog(context, datagramPacket);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.INCONSISTENT_LOG, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals("3", params.get(0));
    }

    @Test
    void test_sendUpdateInconsistentLogMessage(){

        Entry entry = new Entry(1,2,3,4);
        context.setStorageIndex(3);

        SendMessageUtils.updateInconsistentLog(context, datagramPacket,entry);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.APPEND, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals(entry.indexStr(), params.get(0));
        assertEquals(entry.termStr(), params.get(1));
        assertEquals(entry.idStr(), params.get(2));
        assertEquals(entry.valueStr(), params.get(3));
        assertFalse(Boolean.valueOf(params.get(4)));
        assertEquals(Constants.UPDATE, params.get(5));
    }

    @Test
    void test_acceptSetMessage(){

        context.setLeader(hosts.get(0));
        Entry entry = new Entry(1,2,3,4);

        SendMessageUtils.acceptSetMessage(context, datagramPacket, entry);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.SET_ACCEPTED, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals("3", params.get(0));
        assertEquals("4", params.get(1));
    }

    @Test
    void test_sendResponseGetMessage_notFound(){

        context.setLeader(hosts.get(0));

        SendMessageUtils.sendResponseGetMessage(context, datagramPacket, null, 3);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.GET_NOT_FOUND, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals("3", params.get(0));
    }

    @Test
    void test_sendResponseGetMessage_found(){

        context.setLeader(hosts.get(0));
        Entry entry = new Entry(1,2,3,4);

        SendMessageUtils.sendResponseGetMessage(context, datagramPacket, entry, 3);
        List<String> output = parseMessage(out.toString());

        assertEquals(Constants.SENT, output.get(0));
        assertEquals(Constants.GET_FOUND, output.get(1));
        assertEquals(port, Integer.parseInt(output.get(2)));

        List<String> params = Arrays.asList(output.get(3).split("\\s+"));
        assertEquals("3", params.get(0));
        assertEquals("4", params.get(1));
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

