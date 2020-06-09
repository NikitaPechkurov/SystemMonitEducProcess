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
    
    Message current;//текущее сообщение

    ClientSocket clientSocket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        student = new User("2","Студент","student","student");//интерпретация студента
        clientSocket = new ClientSocket();
    }

    public void connect(ActionEvent actionEvent) throws InterruptedException, IOException{
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
        String comment = commentSide.getText();
        commentSide.setText("");
        System.out.println("Comment client: "+comment);
        sendRecord(student.getId(),current.getId_slide(),comment,"commentClient");
        TextAreaClient.appendText("Sl: "+current.getId_slide()+", user: "+student.getUsername()+", type: comment, mes: "+comment+"\r\n");
    }

    public void OKQuestion(ActionEvent actionEvent) throws InterruptedException, IOException{
        String question = questionSide.getText();
        questionSide.setText("");
        System.out.println("Вопрос client: "+question);
        sendRecord(student.getId(),current.getId_slide(),question,"question");
        TextAreaClient.appendText("Sl: "+current.getId_slide()+", user: "+student.getUsername()+", type: question, mes: "+question+"\r\n");
    }

    public void updateClientChat(ActionEvent actionEvent) throws InterruptedException, IOException{
        //TextAreaClient.setText(" ");
        clientSocket.setMessage(new Message(student.getId(),current.getId_slide(),"","updateClientChat"));
        clientSocket.setFlag(true);
        Thread.sleep(1000);
        answers = clientSocket.getAnswers();
        TextAreaClient.appendText(" "+answers);
        System.out.println("Ответы обновлены!");
    }

    //***ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ***
    private void sendRecord(String username, String nn, String mes, String type) throws InterruptedException, IOException{
        clientSocket.setMessage(new Message(username,nn,mes,type));
        clientSocket.setFlag(true);
        Thread.sleep(10);
    }
    //***
}
