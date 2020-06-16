package Client;

import Connection.DAOUser;
import Model.ImageVision;
import Model.Message;
import Model.User;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    /**
     * Класс, предназначенный для обработки событий главного окна клиента.
     * Свойства класса - элементы, содержащиеся на форме окна.
     * @author Nikita Pechkurov
     * *@version 2.3.3
     */
    public Button connectButton;
    public ImageView imageViewClient;
    public TextField questionSide;
    public TextField commentSide;
    public Button OKCommentButt;
    public Button OKQuestion;
    public String answers;
    public Button nextButton;
    public Button updateClientChatButt;
    public TextArea TextAreaClient;
    User student;//студент (пользователь)
    
    Message current;//текущее сообщение
    String TextThereClient;//переменная накопления сообщений на клиента (оптимизация чата)

    ClientSocket clientSocket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * Вызывается для инициализации контроллера после того,
         * как его корневой элемент был полностью обработан.
         */
        student = new User("2","Студент","student","student");//интерпретация студента
        clientSocket = new ClientSocket();
    }

    public void connect(ActionEvent actionEvent) throws InterruptedException, IOException{
        /**
         * Метод, обрабатывающий события нажатия кнопки "Подключиться".
         * Посылает запрос на сервер для получения текущего слайда презентации
         * и устанавлиает слайд в контейнер для картинки слайда.
         */
        connectButton.setDisable(true);
        clientSocket.setMessage(new Message(student.getId(),"","","getSlide"));
        clientSocket.setFlag(true);
        System.out.println("Отправлено сообщение с просьбой передать текущий слайд");
        clientSocket.start();
        Thread.sleep(1000);
        current = clientSocket.getMessage();
        ImageVision imageOneClient = current.getImageVision();//полученная картинка первого слайда
        imageViewClient.setImage(SwingFXUtils.toFXImage(imageOneClient.getImage(),null));
    }

    public void onNextImage(ActionEvent actionEvent) {
        /**
         * Метод, обрабатывающий события нажатия кнопки ">>" (следующий слайд).
         * Посылает запрос на сервер для получения следующего слайда презентации
         * и устанавлиает слайд в контейнер для картинки слайда.
         */
        try {
            clientSocket.setMessage(new Message(student.getId(),"","","getSlide"));
            clientSocket.setFlag(true);
            System.out.println("Отправлено сообщение с просьбой передать текущий слайд");
            Thread.sleep(1000);
            current = clientSocket.getMessage();
            ImageVision imageOneClient = current.getImageVision();//полученная картинка первого слайда
            imageViewClient.setImage(SwingFXUtils.toFXImage(imageOneClient.getImage(),null));
            System.out.println("Изображение получено!");
        }catch (Exception e){
            System.out.println("Нужной картинки, видимо, не пришло или сервер неактивен!: "+e);
        }
    }

    public void OKCommentClient(ActionEvent actionEvent) throws InterruptedException, IOException{
        /**
         * Метод, обрабатывающий события нажатия кнопки отправления комментария.
         * Отправляет сообщение с комментарием на сервер и устанавливает
         * введенное сообщение в чат клиента для наглядности.
         */
        String comment = commentSide.getText();
        commentSide.setText("");
        System.out.println("Comment client: "+comment);
        sendRecord(student.getId(),current.getId_slide(),comment,"commentClient");
        //накопили свой текст
        TextThereClient += "Sl: "+current.getId_slide()+", user: "+student.getUsername()+", type: comment, mes: "+comment+"\r\n";
        TextAreaClient.setText(TextThereClient);//заменили на накопленный
    }

    public void OKQuestion(ActionEvent actionEvent) throws InterruptedException, IOException{
        /**
         * Метод, обрабатывающий события нажатия кнопки отправления вопроса.
         * Отправляет сообщение с вопросом на сервер и устанавливает
         * введенное сообщение в чат клиента для наглядности.
         */
        String question = questionSide.getText();
        questionSide.setText("");
        System.out.println("Вопрос client: "+question);
        sendRecord(student.getId(),current.getId_slide(),question,"question");
        TextThereClient += "Sl: "+current.getId_slide()+", user: "+student.getUsername()+", type: question, mes: "+question+"\r\n";
        TextAreaClient.setText(TextThereClient);//заменили на накопленный
    }

    public void updateClientChat(ActionEvent actionEvent) throws InterruptedException, IOException{
        /**
         * Метод, обрабатывающий события нажатия кнопки "Обновить" на форме клиента.
         * Отсылает сообщение на сервер с просьбой прислать последние данные
         * с ответами преподавателя. Устанавливает полученные данные в окно чата.
         */
        //TextAreaClient.setText(" ");
        clientSocket.setMessage(new Message(student.getId(),current.getId_slide(),"","updateClientChat"));
        clientSocket.setFlag(true);
        Thread.sleep(1000);
        //для оптимизации:
        if (clientSocket.getMessage().getMessage()!=null) {
            TextThereClient = TextThereClient + clientSocket.getMessage().getMessage();//заново создаем полный текст чата
            TextAreaClient.setText(TextThereClient);
            System.out.println("Ответы обновлены!");
        } else TextAreaClient.setText("Преподаватель завершил презентацию!");
    }

    //***ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ***
    private void sendRecord(String username, String nn, String mes, String type) throws InterruptedException, IOException{
        /**
         * Метод для отправки сообщения на сервер. Сообщение
         * формируется из строк, переданных методу
         * в качестве параметров.
         */
        clientSocket.setMessage(new Message(username,nn,mes,type));
        clientSocket.setFlag(true);
        Thread.sleep(10);
    }
    //***
}
