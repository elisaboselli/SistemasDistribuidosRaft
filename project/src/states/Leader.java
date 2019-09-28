package states;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.TimerTask;

import context.Context;
import messages.HeartBeat;
import messages.Message;
import utils.Host;
import utils.JSONUtils;
import utils.UDPUtils;

public class Leader {
	
	static State execute(Context context){
	    //try {
	        // Datagram Socket
	        byte[] buffer = new byte[1000];
	        // Heart Beat Sender
	        TimerTask timerTask = new TimerTask() {
	          @Override
	          public void run() {
	            Leader.sendHeartBeat(context);
	          }
	        };
	        timerTask.run();
	        /*while (true) {
	          // Receive a message
	          DatagramPacket receivedMessage = new DatagramPacket(buffer, buffer.length);
	          context.getServerSocket().receive(receivedMessage);

	          Message message = JSONUtils.getMessage(receivedMessage);
	          UDPUtils.logMessageArrive(receivedMessage, message, context.getPort());
	        }
	    } catch (SocketException e) {
	    	System.out.println("Socket: " + e.getMessage());
	    } catch (IOException e) {
	    	System.out.println("IO: " + e.getMessage());
	    }*/
	    return newEvent();
	}
	
	static State newEvent() {
		return null;
	}
	
    public static void sendHeartBeat(Context context) {
    	try {
    		for (Host host : context.getAllHosts()) {
    			HeartBeat heartBeatMessage = new HeartBeat(context.getPort(), host.getPort());
    			DatagramPacket heartBeat = new DatagramPacket(heartBeatMessage.toJSON().getBytes(),
    					heartBeatMessage.toJSON().length(), host.getAddress(), host.getPort());
    			context.getServerSocket().send(heartBeat);
    		}
    	} catch (IOException e) {
    		System.out.println("IO Exception: " + e.getMessage());
    	}
    }
	
}
