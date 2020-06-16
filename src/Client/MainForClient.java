package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainForClient extends Application {
    /**
     * Главный класс приложения слушателя,
     * предназначенный для запуска потока окна приложения.
     * @param primaryStage
     * @throws Exception
     * @author Nikita Pechkurov
     * *@version 2
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        /**
         * Метод, запускающий окно приложения
         */
        Parent root = FXMLLoader.load(getClass().getResource("sampleClient.fxml"));
        primaryStage.setTitle("Client Window");
        primaryStage.setScene(new Scene(root, 700, 450));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
