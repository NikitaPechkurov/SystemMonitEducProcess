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

    Image img;//текущий слайд - картинка. Слайдов 7. БД содержит ссылки на картинки в виде сообщений для клиента,
    //идентификация по типу сообщения и номеру слайда
    Message T;//текущее сообщение

    ClientSocket clientSocket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        student = new User("1","Студент","student","student");
        clientSocket = new ClientSocket();
    }

    public void connect(ActionEvent actionEvent) throws InterruptedException, IOException{
        connectButton.setDisable(true);
        clientSocket.setMessage(new Message(student.getId(),"","","getSlide"));
        System.out.println("Отправлено сообщение с просьбой передать текущий слайд");
        clientSocket.start();
        Thread.sleep(2000);
        T = clientSocket.getMessage();
        ImageVision imageOneClient = T.getImageVision();//полученная картинка первого слайда
        imageViewClient.setImage(SwingFXUtils.toFXImage(imageOneClient.getImage(),null));
        //clientSocket.stop();
        //System.out.println("Картинка установлена в ImageViewClient! " + imageOneClient.impl_getUrl());
    }

    public void onNextImage(ActionEvent actionEvent) {
        try {
            clientSocket.setMessage(new Message(student.getId(),"","","getSlide"));
            System.out.println("Отправлено сообщение с просьбой передать текущий слайд");
            //clientSocket.start();
            Thread.sleep(5000);
            T = clientSocket.getMessage();
            ImageVision imageOneClient = T.getImageVision();//полученная картинка первого слайда
            imageViewClient.setImage(SwingFXUtils.toFXImage(imageOneClient.getImage(),null));
            System.out.println("Изображение получено!");
            //clientSocket.stop();
        }catch (Exception e){
            System.out.println("Нужной картинки, видимо, не пришло или сервер неактивен!: "+e);
        }
    }

    public void OKCommentClient(ActionEvent actionEvent) throws InterruptedException, IOException{
        String comment = commentSide.getText();
        commentSide.setText("");
        System.out.println("Comment client: "+comment);
        sendRecord(student.getId(),T.getId_slide(),comment,"commentClient");
        TextAreaClient.appendText(student.getUsername()+": "+comment+", comment;\r\n");
    }

    public void OKQuestion(ActionEvent actionEvent) throws InterruptedException, IOException{
        String question = questionSide.getText();
        questionSide.setText("");
        System.out.println("Вопрос client: "+question);
        sendRecord(student.getId(),T.getId_slide(),question,"question");
        TextAreaClient.appendText(student.getUsername()+": "+question+", question;\r\n");
    }

    public void updateClientChat(ActionEvent actionEvent) throws InterruptedException, IOException{
        TextAreaClient.setText("");
        clientSocket.setMessage(new Message(student.getId(),T.getId_slide(),"","updateClientChat"));
        Thread.sleep(9000);
        answers = clientSocket.getAnswers();
        TextAreaClient.appendText(answers);
        System.out.println("Ответы обновлены!");
    }

    //***ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ***
    /*private Message getSlideMessage(String id_user) throws InterruptedException{
        clientSocket.setMessage(new Message(id_user,"","","getSlide"));
        System.out.println("Отправлено сообщение с просьбой передать текущий слайд");
        Thread.sleep(12000);
        Message t = clientSocket.getMessage();
        return t;
    }*/

    private void sendRecord(String username, String nn, String mes, String type) throws InterruptedException, IOException{
        clientSocket.setMessage(new Message(username,nn,mes,type));
        Thread.sleep(10);
    }
    //***
}
