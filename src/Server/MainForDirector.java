package Server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainForDirector extends Application {
    /**
     * Главный класс приложения преподавателя,
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
        Parent root = FXMLLoader.load(getClass().getResource("sampleDirector.fxml"));
        primaryStage.setTitle("Director Window");
        primaryStage.setScene(new Scene(root, 745, 470));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }


}