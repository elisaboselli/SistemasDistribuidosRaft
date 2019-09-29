package states;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import context.Context;
import messages.HeartBeat;
import utils.Constants;
import utils.Host;

public class Leader {
	
	static State execute(Context context){
	    //try {
	        // Datagram Socket
	        byte[] buffer = new byte[1000];
	        // Heart Beat Sender
	        Timer timer = new Timer();
	        TimerTask hearbeat = new TimerTask() {
	          @Override
	          public void run() {
	            Leader.sendHeartBeat(context);
	          }
	        };
	        timer.schedule(hearbeat, Constants.HEART_BEAT);
	        //TODO puede llegar un mensaje de un candidato con un termino mayor
	        //TODO puede 
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
