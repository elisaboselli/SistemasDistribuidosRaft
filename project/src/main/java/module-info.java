module project {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.json;


    opens userinterface to javafx.fxml;
    exports userinterface;
}