package states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

import context.Context;
import utils.Constants;

public class Follower {
	
	static State newtEvent() {
		return null;
	}

	static State execute(Context context) {
		DatagramSocket socket= context.getServerSocket();
        Random random = new Random();
		try {
			socket.setSoTimeout(random.ints(1, Constants.MIN_TIMEOUT, Constants.MAX_TIMEOUT).findFirst().getAsInt());
			while(true) {
			    byte[] buffer = new byte[1000];
			    DatagramPacket getack = new DatagramPacket(buffer, buffer.length);
			    try {
			        socket.receive(getack);
			    } catch (SocketTimeoutException e) {
			       // resend
			       break;
			    } catch (IOException e) {
					e.printStackTrace();
					return State.FOLLOWER;
				}
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//poner el timeout en 0 
		return State.CANDIDATE;
	}

}
