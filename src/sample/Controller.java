package sample;

import Model.DAOMessage;
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

public class Controller implements Initializable{

    public TextArea TextAreaDirector;
    public Button endPresentButt;
    public Button sldLeftButt;
    public Button sldRightButt;
    public Button OKCommentDir;
    public Button OKAnswer;
    public Button beginButt;
    public TextField commentDirectorSide;
    public ImageView imageViewDirector;
    public Button updateButton;
    public String questions;
    public TextField answerSide;
    User lector;//лектор

    int N = 1;//номер текущего слайда
    static Image img;//текущий слайд - картинка. Слайдов 7. БД содержит ссылки на картинки в виде сообщений для клиента,
    //идентификация по типу сообщения и номеру слайда

    MultiThreadServer server;//сервер

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            lector = DAOUser.searchUser("Василий Петрович").get(0);
            server = new MultiThreadServer();
            server.start();
        }catch(SQLException e){
            System.out.println(e);
        }catch (ClassNotFoundException ec){
            System.out.println(ec);
        }
    }

    public void begin(ActionEvent actionEvent) throws SQLException, ClassNotFoundException{
        try {
            //**ЗАГРУЗКА ПЕРВОЙ КАРТИНКИ
            //**установка картинки
            /*img = getImage(String.valueOf(N));
            imageViewDirector.setImage(img);*/
            Message current = new Message("1","1",getImageMessage(String.valueOf(N)).getMessage(),"image");
            imageViewDirector.setImage(new Image(current.getMessage()));
            server.setMessage(current);
            System.out.println("Картинка установлена в imageViewDirector!, установлена в server");
            //**смена значения текущего слайда на 1
            currentSlide(String.valueOf(N));
            //**
        }catch(InterruptedException e){
            System.out.println("Interrupted Exception in Controller.begin(): "+e);
        }
    }

    public void slideLeft(ActionEvent actionEvent) {
        N--;//декрементируем счетчик слайда
        try {
            //***смена значения текущего слайда
            currentSlide(String.valueOf(N));
            //***
            //***установка картинки
            //img = getImage(String.valueOf(N));
            imageViewDirector.setImage(img);
            server.setMessage(DAOMessage.searchImageMessage(String.valueOf(N)));
            System.out.println("Картинка установлена в imageViewDirector!");
            //*****
        }catch (Exception e){
            System.out.println("Exception in slideLeft(): "+e);
        }
    }

    public void slideRight(ActionEvent actionEvent) {
        N++;//инкрементируем счетчик слайда
        try {
            //***смена значения текущего слайда
            currentSlide(String.valueOf(N));
            //**
            //***установка картинки
            //img = getImage(String.valueOf(N));
            imageViewDirector.setImage(img);
            server.setMessage(DAOMessage.searchImageMessage(String.valueOf(N)));
            System.out.println("Картинка установлена в imageViewDirector!");
            //*****
        }catch (Exception e){
            System.out.println("Exception in slideRight(): "+e);
        }
    }

    public void OKCommentDirect(ActionEvent actionEvent) throws InterruptedException, SQLException, ClassNotFoundException{
        String comment = commentDirectorSide.getText();
        commentDirectorSide.setText("");
        System.out.println("Comment director: "+comment);
        sendRecord(lector.getId(),String.valueOf(N),comment,"comment");
        TextAreaDirector.appendText("Sl:"+N+". "+lector.getUsername()+": "+comment+", comment;\r\n");
    }

    public void OKAnswer(ActionEvent actionEvent) throws InterruptedException, SQLException, ClassNotFoundException{//ответ Director
        String message = answerSide.getText();
        answerSide.setText("");
        System.out.println("Ответ: "+message);
        sendRecord(lector.getId(),String.valueOf(N),message,"answer");
        TextAreaDirector.appendText("Sl:"+N+". "+lector.getUsername()+": "+message+", answer;\r\n");
    }

    public void endPresent(ActionEvent actionEvent) throws SQLException, ClassNotFoundException{
        //устанавливаем счетчик в несуществующее положение
        currentSlide("999");
        server.executeIt.shutdown();
    }

    public void update(ActionEvent actionEvent) throws SQLException, ClassNotFoundException{//update Director Chat
        TextAreaDirector.setText("");
        String questions = DAOMessage.searchListMessage("2","question");
        questions += DAOMessage.searchListMessage("2","comment");
        TextAreaDirector.appendText(questions);
        System.out.println("Вопросы обновлены!");
    }

    //***вспомогательные функции
    private void sendRecord(String username, String nn, String mes, String type) throws InterruptedException, SQLException, ClassNotFoundException{
        DAOMessage.insertMessage(new Message(username,nn,mes,type));
        Thread.sleep(10);
        System.out.println("Сообщение было записано в БД!");
    }

    private Message getImageMessage(String id_slide) throws InterruptedException, SQLException, ClassNotFoundException{
        Message t = DAOMessage.searchImageMessage(id_slide);
        Thread.sleep(12000);//задержка расчитана экспериментально
        return t;
    }

    private void currentSlide(String id_slide) throws SQLException, ClassNotFoundException{
        DAOMessage.updateSlideNumber(id_slide);
        System.out.println("Значение текущего слайда обновлено");
    }
    //***

}
