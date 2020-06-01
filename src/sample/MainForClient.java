package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainForClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sampleClient.fxml"));
        primaryStage.setTitle("Client Window");
        primaryStage.setScene(new Scene(root, 700, 450));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }


}
