package userinterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("client-view.fxml"));
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
