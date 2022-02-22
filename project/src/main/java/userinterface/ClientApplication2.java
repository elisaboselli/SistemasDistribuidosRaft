package userinterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import utils.Constants;
import utils.JSONUtils;
import utils.Message;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ClientApplication2 extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("client-view.fxml"));
        FlowPane flowPane = fxmlLoader.load();

        ClientController controller = fxmlLoader.getController();
        controller.setUp();

        Scene scene = new Scene(flowPane, 320, 400);
        scene.getRoot().setStyle("-fx-font-family: 'Arial'");
        stage.setTitle("Client App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String args[]) {
        launch(args);
    }
}
