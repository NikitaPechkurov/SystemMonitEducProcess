package Server;

import Connection.DAOMessage;
import Connection.DAOUser;
import Model.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    /**
     * Класс, предназначенный для обработки событий главного окна сервера.
     * Свойства класса - элементы, содержащиеся на форме окна.
     * @author Nikita Pechkurov
     * *@version 2.3.3
     */
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
    public TextField answerSide;
    //оптимизация чата
    public String TEXT;//отображаемый в окне текст
    public String TextThere;//для накопления записанных преподавателем ответов и комм
    User lector;//лектор

    Message current;//текущее сообщение

    MultiThreadServer server;//сервер
    ImageCollection imgCol;
    Iterator iterator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * Вызывается для инициализации контроллера после того,
         * как его корневой элемент был полностью обработан.
         */
        try {
            lector = DAOUser.searchUser("Василий Петрович").get(0);
            server = new MultiThreadServer();
            imgCol = new ImageCollection();
        }catch(SQLException e){
            System.out.println(e);
        }catch (ClassNotFoundException ec){
            System.out.println(ec);
        }catch (IOException eo){
            System.out.println(eo);
        }
    }

    public void begin(ActionEvent actionEvent) throws SQLException, ClassNotFoundException{
        /**
         * Метод обработки события нажатия кнопки "Начать презентацию".
         * Устанавливает первый слайд в контейнер для картинки слайда,
         * запускает сервер.
         */
        try {
            //инициализация итератора
            iterator = imgCol.getIterator();
            //**установка картинки
            current = new Message("1",String.valueOf(imgCol.getCurrent()),"begin","image");
            if (iterator.hasNext()) {
                ImageVision imageOne = (ImageVision) iterator.next();
                imageViewDirector.setImage(SwingFXUtils.toFXImage(imageOne.getImage(),null));
                current.setImageVision(imageOne);
                server.setMessage(current);
                System.out.println("Картинка установлена в imageViewDirector, слайд: "+imgCol.getCurrent());
            }
            else System.out.println("Следующей картинки не существует!");
            server.start();
            System.out.println("MultiThreadServer запущен!");
        }catch(Exception e){
            System.out.println("Interrupted Exception in Controller.begin(): "+e);
        }
    }

    public void slideLeft(ActionEvent actionEvent) {
        /**
         * Метод обработки события нажатия кнопки "Слайд влево".
         * Устанавливает предыдущий слайд в окно для кратинки слайда.
         */
        try {
            ImageVision image = (ImageVision) iterator.preview();
            imageViewDirector.setImage(SwingFXUtils.toFXImage(image.getImage(),null));
            current = new Message("1",String.valueOf(imgCol.getCurrent()),"slideLeft","image");
            current.setImageVision(image);
            server.setMessage(current);
            System.out.println("Картинка установлена в imageViewDirector и передана на сервер, слайд: "+imgCol.getCurrent());
        }catch (Exception e){
            System.out.println("Exception in slideLeft(): "+e);
        }
    }

    public void slideRight(ActionEvent actionEvent) {
        /**
         * Метод обработки события нажатия кнопки "Слайд право".
         * Устанавливает следующий слайд в окно для кратинки слайда.
         */
        try {
            if (iterator.hasNext()){
                ImageVision image = (ImageVision) iterator.next();
                imageViewDirector.setImage(SwingFXUtils.toFXImage(image.getImage(),null));
                current = new Message("1",String.valueOf(imgCol.getCurrent()),"slideRight","image");
                current.setImageVision(image);
                server.setMessage(current);
                System.out.println("Картинка установлена в imageViewDirector!");
                TextThere = "";
                TextAreaDirector.setText(TextThere);
            }
            else System.out.println("Следующей картинки не существует!");
            System.out.println("Картинка установлена в imageViewDirector и передана на сервер, слайд: "+imgCol.getCurrent());
        }catch (Exception e){
            System.out.println("Exception in slideRight(): "+e);
        }
    }

    public void update(ActionEvent actionEvent) throws SQLException,
            ClassNotFoundException, IOException{
        /**
         * Метод обработки нажатия кнопки "Обновить".
         * Добавляет текст с сервера в чат.
         */
        //TextAreaDirector.setText(" ");
        TextThere = TextThere + server.getTextForServer();//присоединили накомленное на сервере
        TextAreaDirector.setText(TextThere);//выводим уже ВСЁ
        System.out.println("Вопросы обновлены!");
        //insertingMessageToDB(current);
    }

    public void OKCommentDirect(ActionEvent actionEvent) throws InterruptedException, SQLException,
            ClassNotFoundException, IOException{
        /**
         * Метод обработки нажатия кнопки отправки комментария на сервер.
         * Также данный метод накапливает данные во внутренюю переменную
         * для удобного отображения данных в чате преподавателя.
         */
        String comment = commentDirectorSide.getText();
        commentDirectorSide.setText("");
        System.out.println("Записан комментарий: "+comment);
        server.addTextForClient("Sl: "+String.valueOf(imgCol.getCurrent())+", user: "+
        DAOUser.searchUserFromId(lector.getId()).getUsername()+", type: comment, mes: "+comment+"\r\n");
        TextThere += "Sl: "+String.valueOf(imgCol.getCurrent())+", user: "+lector.getUsername()
                +", type: comement, ,mes: "+comment+"\r\n";//накапливаем у себя
        TextAreaDirector.setText(TextThere);//выводим
        //DAOMessage.insertMessage(new Message(lector.getId(),String.valueOf(imgCol.getCurrent()),comment,"comment"));
    }

    public void OKAnswer(ActionEvent actionEvent) throws InterruptedException, SQLException,
            ClassNotFoundException, IOException{
        /**
         * Метод обработки нажатия кнопки отправки ответа на сервер.
         * Также данный метод накапливает данные во внутренюю переменную
         * для удобного отображения данных в чате преподавателя.
         */
        System.out.println("TextThere: "+TextThere+"\r\n");
        String answer = answerSide.getText();
        answerSide.setText("");
        System.out.println("Записан ответ: "+answer);
        server.addTextForClient("Sl: "+String.valueOf(imgCol.getCurrent())+", user: "+
                DAOUser.searchUserFromId(lector.getId()).getUsername()+", type: answer, mes: "+answer+"\r\n");
        TextThere += "Sl: "+String.valueOf(imgCol.getCurrent())+",user: "+lector.getUsername()
                +", type: answer, mes: "+answer+"\r\n";//накапливаем у себя
        TextAreaDirector.setText(TextThere);//выводим
        //DAOMessage.insertMessage(new Message(lector.getId(),String.valueOf(imgCol.getCurrent()),answer,"answer"));
    }

    public void endPresent(ActionEvent actionEvent) throws SQLException, ClassNotFoundException{
        /**
         * Метод завершения презентации. Останавливает сервер.
         */
        if (server.isAlive()){
            server.interrupt();
            server.nullTextForClient();
        }
        else System.out.println("Сервер уже закрыт!");
    }

    //***вспомогательные функции
    private void insertingMessageToDB(Message entry) throws IOException, SQLException, ClassNotFoundException{
        /**
         * Метод вставки данных в базу данных.
         * В качестве аргумента принимает сообщение для вставки.
         * Необходим для повторного использования с выведением сообщения
         * успешного добавления данных.
         */
        //*******ДОБАВКА СООБЩЕНИЯ В БД
        DAOMessage.insertMessage(entry);
        System.out.println("ServerController записал сообщение в БД!"+entry);
    }
    //***

}
