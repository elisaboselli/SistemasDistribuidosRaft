import context.Context;
import org.junit.jupiter.api.Test;
import states.Leader;
import utils.Constants;
import utils.Host;
import utils.Message;

import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


class LeaderTest {
    private File storageFile;
    private File logFile;
    private Context context;
    private Host host;

    @Test
    void testProcessMessage_postulation() {

        try {
            storageFile = new File(String.valueOf(6787));
            logFile = new File(String.valueOf(6787));
            context = new Context(6787, storageFile.getName(), logFile.getName(),true);
            host = new Host(InetAddress.getByName("localhost"), 6787);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        Message message = new Message(1, Constants.POSTULATION, 1234, context.getPort(), null);
        String messageStr = message.toJson();
        DatagramPacket postulationMessage = new DatagramPacket(messageStr.getBytes(), messageStr.length(), host.getAddress(), host.getPort());

        Leader.processMessage(context, postulationMessage);
    }
}
