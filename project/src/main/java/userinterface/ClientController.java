package userinterface;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.Constants;
import utils.JSONUtils;
import utils.Message;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController{
    @FXML
    private TextField fieldPort;
    @FXML
    private TextField fieldAddress;
    @FXML
    private TextField fieldId;
    @FXML
    private TextField fieldValue;
    @FXML
    private Label resultText;
    @FXML
    private Button connectButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private Button getButton;
    @FXML
    private Button setButton;
    @FXML
    private Button exitButton;

    private int localPort;
    private int serverPort;
    private String localAddress;
    private String serverAddress;
    private DatagramSocket socketUDP;
    private InetAddress hostServer;
    private File logFile;

    public void setUp() {
        try {
            localPort = 6786;
            localAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onConnectButtonClick() {
        serverPort = Integer.parseInt(fieldPort.getText());
        serverAddress = fieldAddress.getText();

        try {
            socketUDP = new DatagramSocket(localPort);
            hostServer = InetAddress.getByName(serverAddress);
            logFile = JSONUtils.createLogFile(String.valueOf(localPort), false);

            System.out.println("\n---------------------------- BEGIN CLIENT ----------------------------\n\n");
            System.out.println("Connected to: " + serverAddress + ":" + serverPort + "\n");

            fieldPort.setDisable(true);
            fieldAddress.setDisable(true);
            connectButton.setDisable(true);
            disconnectButton.setDisable(false);

            fieldId.setDisable(false);
            fieldValue.setDisable(false);
            getButton.setDisable(false);
            setButton.setDisable(false);

        }catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }

    @FXML
    protected void onDisconnectButtonClick() {
        System.out.println("Disconnected from: " + serverAddress + ":" + serverPort + "\n");
        socketUDP.close();

        fieldPort.setDisable(false);
        fieldAddress.setDisable(false);
        connectButton.setDisable(false);
        disconnectButton.setDisable(true);

        fieldId.setDisable(true);
        fieldValue.setDisable(true);
        getButton.setDisable(true);
        setButton.setDisable(true);
    }

    @FXML
    protected void onGetButtonClick() {
        String id = fieldId.getText();
        List<String> params =  Arrays.asList(id);
        String messageType = Constants.GET;
        Message requestMessage = new Message(Constants.NO_TERM, messageType, localPort, localAddress, serverPort, serverAddress, params);
        requestMessage.log(localPort, false, logFile.getName());

        Message response = sendMessage(requestMessage);

        if(response == null) {
            resultText.setText("Error");
            return;
        }

        params = response.getParams();
        String result = "< id : " + params.get(0) + " , value : ";
        if(response.getType().equals(Constants.GET_NOT_FOUND)){
            result += " Not Found >";
        } else {
            result += params.get(1) + " >";
        }
        resultText.setText(result);
    }

    @FXML
    protected void onSetButtonClick() {
        String id = fieldId.getText();
        String value = fieldValue.getText();

        List<String> params =  Arrays.asList(id,value);
        String messageType = Constants.SET;
        Message requestMessage = new Message(Constants.NO_TERM, messageType, localPort, localAddress, serverPort, serverAddress, params);
        requestMessage.log(localPort, false, logFile.getName());

        resultText.setText("Set (" + id + "," + value  + ") !");

        Message response = sendMessage(requestMessage);
    }

    @FXML
    public void onExitButtonClick(ActionEvent event) {
        if(socketUDP != null && !socketUDP.isClosed()) {
            System.out.println("\n---------------------------- CLOSING CLIENT ---------------------------\n");
            socketUDP.close();
        }
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private Message sendMessage(Message requestMessage){
        DatagramPacket response = null;
        Message error = null;

        // Prepare datagram package
        String requestMessageStr = requestMessage.toJson();
        DatagramPacket request = new DatagramPacket(requestMessageStr.getBytes(), requestMessageStr.length(),
                hostServer, serverPort);

        try {
            // Send message
            socketUDP.send(request);

            // Receive response
            byte[] buffer = new byte[1000];
            response = new DatagramPacket(buffer, buffer.length);
            socketUDP.receive(response);

        } catch (IOException e) {
            e.printStackTrace();
            //error = new Message(e.getMessage());
        }

        // Parse Response
        if (response != null) {
            Message responseMessage = JSONUtils.messageFromJson(response);
            responseMessage.log(localPort, true, logFile.getName());
            return responseMessage;
        }

        return error;

    }
}