package Model;


import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

public class Message implements Serializable{
    /**
     * Класс, представляющий реализацию хранимого в базе данных
     * и отправляемого от клиента серверу и наоборот сообщения,
     * хранящего в себе изображение, текст, номер слайда, номер пользователя,
     * отправившего сообщение.
     * Содержит методы для получения и установки данных в сообщение.
     * @author Nikita Pechkurov
     * *@version 2
     */
    String id;
    String id_user;
    String id_slide;
    String message;
    String type;
    ImageVision image;

    public Message(String id_user, String id_slide, String message, String type) throws IOException{
        /**
         * Конструктор класса. Принимает в качестве параметров id пользователя,
         * id слайда, текст сообщения и тип сообщения.
         */
        this.id_user = id_user;
        this.id_slide = id_slide;
        this.message = message;
        this.type = type;
        image = new ImageVision(ImageIO.read(new URL("https://i.imgur.com/L0iDcra.png")));
    }

    public String getId() {
        /**
         * Метод для возврата id сообщения.
         */
        return id;
    }

    public void setId(String id) {
        /**
         * Метод для установки id сообщения.
         */
        this.id = id;
    }

    public String getId_user() {
        /**
         * Метод для возврата id пользователя.
         */
        return id_user;
    }

    public void setId_user(String id_user) {
        /**
         * Метод для установки id пользователя.
         */
        this.id_user = id_user;
    }

    public String getId_slide() {
        /**
         * Метод для получения номера слайда.
         */
        return id_slide;
    }

    public void setId_slide(String id_slide) {
        /**
         * Метод для установки в сообщения номера слайда.
         */
        this.id_slide = id_slide;
    }

    public String getMessage() {
        /**
         * Метод для получения текста сообщения.
         */
        return message;
    }

    public void setMessage(String message) {
        /**
         * Метод для установки текста сообщения.
         */
        this.message = message;
    }

    public String getType() {
        /**
         * Метод для получения типа сообщения.
         */
        return type;
    }

    public void setType(String type) {
        /**
         * Метод для установки типа сообщения.
         */
        this.type = type;
    }

    public ImageVision getImageVision(){
        /**
         * Метод для получения пиксельного представления изображения.
         */
        return image;
    }

    public void setImageVision(ImageVision image){
        /**
         * Метод для установки пиксельного представления изображения.
         */
        this.image = image;
    }

    public String record(){
        /**
         * Метод для вывода данных сообщения для базы данных.
         */
        return "'"+id_user+"','"+id_slide+"','"+message+"','"+ type+"'";
    }

    public String toString(){
        /**
         * Метод для вывода данных сообщения в поля форм.
         */
        return id_user+", "+id_slide+", "+message+", "+ type;
    }
}
