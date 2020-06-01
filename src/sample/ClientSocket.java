package sample;

import Model.Message;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.*;
import java.net.Socket;

public class ClientSocket extends Thread{

    private static Socket socket;
    private DataOutputStream oos;
    private DataInputStream ois;
    private ObjectOutputStream objos;
    private ObjectInputStream oin;
    private Message message;
    //private Image image = new Image("https://i.imgur.com/L0iDcra.png");
    //private Image image;
    private String questions;
    private String answers;

    public ClientSocket() {
        try {
            // создаём сокет общения на стороне клиента
            socket = new Socket("localhost", 3214);
            System.out.println("New Client connected to socket!");
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try  // создаём объект для записи строк в созданный скокет, для
                // чтения строк из сокета
                // в try-with-resources стиле
                 {
            // создаём объект для записи строк в созданный скокет, для
            // чтения строк из сокета
            oos = new DataOutputStream(socket.getOutputStream());
            ois = new DataInputStream(socket.getInputStream());
                     objos = new ObjectOutputStream(oos);
                     oin = new ObjectInputStream(ois);
            System.out.println("Client DOS & DIS initialized");

            while (!socket.isClosed()) {
                //ЗАПИСАЛИ СООБЩЕНИЕ
                objos.writeObject(message);
                objos.flush();

                System.out.println("ClientSocket: сообщение передано на сервер.");
                System.out.println("reading...");

                //клиент-сокет может попросить загрузить вопрос, комментарий; послать ему картинку, первую картинку; и обновить клиентский чат
                if (message.getType().equals("question") || message.getType().equals("comment")) {
                    listening();
                } else if (/*message.getType().equals("getImage") ||*/ message.getType().equals("getSlide")) {
                    gettingImage();
                } else if (message.getType().equals("updateClientChat")) {
                    answers = (String) oin.readObject();
                    System.out.println("С сервера пришло " + answers.length() + " символов.");
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            System.out.println("ClassNotFound getImage: "+e);
        }
    }

    //***ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ***
    private void listening() throws IOException{
        String in = ois.readUTF();
        System.out.println("Сервер ответил по поводу загрузки вопроса/ответа/комментария/обновления номера слайда: " + in);
    }

    private void gettingImage() throws IOException,ClassNotFoundException,InterruptedException{
        message = (Message) oin.readObject();
        System.out.println("Картинка сформирована!");
        System.out.println("Сервер послал картинку слайда::: " + message.getMessage());
    }
    //***


    //геттеры для возвращения полезных данных
    public void setMessage(Message mes){
        this.message = mes;
    }

    /*public Image getImage(){//возвращать этим методом картинку в контроллер
        return image;
    }*/

    public Message getMessage(){
        return message;
    }

    public String getAnswers(){
        return answers;
    }
    //

}
