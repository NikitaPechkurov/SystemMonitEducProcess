package sample;

import Model.DAOUser;
import Model.Message;
import Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    ClientSocket clientSocket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            student = DAOUser.searchUser("Александр").get(0);
            clientSocket = new ClientSocket();
        }catch(SQLException e){
            System.out.println(e);
        }catch (ClassNotFoundException ec){
            System.out.println(ec);
        }
    }

    public void connect(ActionEvent actionEvent) throws InterruptedException{
        connectButton.setDisable(true);
        Message current = getSlideMessage(student.getId());
        if (current.getMessage() == null) {
            imageViewClient.setImage(new Image("https://i.imgur.com/L0iDcra.png"));
        }
        else {
            System.out.println("Картинка установлена в ImageViewClient!" + current.getMessage());
            imageViewClient.setImage(new Image(current.getMessage()));
        }
    }

    public void OKCommentClient(ActionEvent actionEvent) throws InterruptedException{
        String comment = commentSide.getText();
        commentSide.setText("");
        System.out.println("Comment client: "+comment);
        //sendRecord(student.getId(),String.valueOf(N),comment,"comment");
        //TextAreaClient.appendText("Sl:"+N+". "+student.getUsername()+": "+comment+", comment;\r\n");
    }

    public void OKQuestion(ActionEvent actionEvent) throws InterruptedException{
        String message = questionSide.getText();
        questionSide.setText("");
        System.out.println("Вопрос: "+message);
        //sendRecord(student.getId(),String.valueOf(N),message,"question");
        //TextAreaClient.appendText("Sl:"+N+". "+student.getUsername()+": "+message+", question;\r\n");
    }

    public void onNextImage(ActionEvent actionEvent) {
        //N++;
        try {
            Message current = getSlideMessage(student.getId());
            imageViewClient.setImage(new Image(current.getMessage()));
            System.out.println("Изображение получено!");
        }catch (Exception e){
            System.out.println("Следующей картинки, видимо, не пришло или администратор завершил встречу: "+e);
        }
    }

    public void updateClientChat(ActionEvent actionEvent) throws InterruptedException{
        TextAreaClient.setText("");
        //clientSocket.setMessage(new Message(student.getId(),String.valueOf(N),"","updateClientChat"));
        Thread.sleep(12000);
        answers = clientSocket.getAnswers();
        TextAreaClient.appendText(answers);
        System.out.println("Ответы обновлены!");
    }

    //***ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ***
    private Message getSlideMessage(String id_user) throws InterruptedException{
        clientSocket.setMessage(new Message(id_user,"","","getSlide"));
        Thread.sleep(12000);
        System.out.println("Отправлено сообщение с просьбой передать текущий слайд");
        Message t = clientSocket.getMessage();
        return t;
    }

    private void sendRecord(String username, String nn, String mes, String type) throws InterruptedException{
        clientSocket.setMessage(new Message(username,nn,mes,type));
        Thread.sleep(10);
    }
    //***
}
