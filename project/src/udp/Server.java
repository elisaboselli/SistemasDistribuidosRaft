package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import messages.Message;
import raft.RaftNode;
import raft.RaftTimerTask;
import utils.Constants;
import utils.JSONUtils;
import utils.UDPUtils;

public class Server {

  final static int WAIT_TIME = 60000;
  private static Timer heart_beat_timer;
  private static Timer raft_timer;

  public static void main(String args[]) {

    try {
      // Datagram Socket
      int port = Integer.parseInt(args[0]);
      DatagramSocket serverSocket = new DatagramSocket(port);
      byte[] buffer = new byte[1000];

      // Raft Node
      RaftNode raftNode = new RaftNode(port, serverSocket);

      // Timer
      raft_timer = new Timer();
      raft_timer.schedule(new RaftTimerTask(raftNode), raftNode.getTimeout());

      // Heart Beat Sender
      TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          raftNode.sendHeartBeat();
        }
      };
      heart_beat_timer = new Timer();
      heart_beat_timer.schedule(timerTask, 0, Constants.HEART_BEAT);

      while (true) {

        // Receive a message
        DatagramPacket receivedMessage = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(receivedMessage);

        Message message = JSONUtils.getMessage(receivedMessage);
        UDPUtils.logMessageArrive(receivedMessage, message, raftNode.getPort());

        // If is a heart beat from leader
        if (message.getType().equals(Constants.HEART_BEAT_MESSAGE)) {
          // Ver si esto anda
          raft_timer.cancel();
          raft_timer = new Timer();
          raft_timer.schedule(new RaftTimerTask(raftNode), raftNode.getTimeout());
          continue;
        }

        // If is a client message
        if (message.getType().equals(Constants.CLIENT_MESSAGE)) {

          if (!raftNode.isLeader()) {
            // Pass message to leader
            processClientMessageAsFollower(message);
            continue;
          } else {
            processClientMessageAsLeader();
            continue;
          }

        } else {

          if (!raftNode.isLeader()) {
            processServerMessageAsFollower();
            continue;
          } else {
            processServerMessageAsLeader();
            continue;
          }

        }

        // int responses = 1;
        // long timeout = System.currentTimeMillis() + WAIT_TIME;

        /*
         * while (responses < Constants.QUORUM && System.currentTimeMillis() < timeout) {
         * DatagramPacket acceptorResponse = new DatagramPacket(buffer, buffer.length);
         * serverSocket.receive(acceptorResponse); // ServerMessage responseMsg = //
         * jsonUtils.getServerMessage(acceptorResponse); responses++; }
         * 
         * if (responses < Constants.QUORUM) System.out.println("TimeOut. Received responses: " +
         * responses); else System.out.println("Quorum OK. Received responses: " + responses);
         * 
         * // Respuesta al cliente /* DatagramPacket response = new
         * DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
         * request.getPort());
         *
         * socketUDP.send(response);
         */
      }

    } catch (SocketException e) {
      System.out.println("Socket: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO: " + e.getMessage());
    }
  }

  private static void processClientMessageAsLeader() {
  }

  private static void processServerMessageAsLeader() {
  }

  private static void processClientMessageAsFollower(Message message) {

  }

  private static void processServerMessageAsFollower() {
  }
}