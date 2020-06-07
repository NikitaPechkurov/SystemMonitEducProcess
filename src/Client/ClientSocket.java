package Client;

import Model.Message;
import javafx.embed.swing.SwingFXUtils;

import java.io.*;
import java.net.Socket;

public class ClientSocket extends Thread{

    private static Socket socket;
    private DataOutputStream oos;
    private DataInputStream ois;
    private ObjectOutputStream objos;
    private ObjectInputStream oin;
    private Message message;//контейнер для Message уже на стороне клиента
    private String answers;

    public ClientSocket() {
        try {
            // создаём сокет общения на стороне клиента
            socket = new Socket("localhost", 3214);
            message = new Message("1","1","begin","image");
            System.out.println("New Client connected to socket!");
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try  // создаём объект для записи сообщений в созданный скокет, для
        // чтения сообщений из сокета
        {
            oos = new DataOutputStream(socket.getOutputStream());
            ois = new DataInputStream(socket.getInputStream());
            objos = new ObjectOutputStream(oos);
            oin = new ObjectInputStream(ois);
            System.out.println("Client DOS & DIS initialized");

            while (!socket.isClosed()) {
                objos.writeObject(message);//СНАЧАЛА ЗАПИСЬ
                objos.flush();
                System.out.println("ClientSocket: сообщение передано на сервер.");
                System.out.println("reading...");
                //ПОТОМ ЧТЕНИЕ!
                //клиент-сокет может попросить загрузить вопрос, комментарий; послать ему картинку, первую картинку; и обновить клиентский чат
                if (message.getType().equals("question") || message.getType().equals("commentClient")) {
                    listening();
                } else if (/*message.getType().equals("getImage") ||*/ message.getType().equals("getSlide")) {
                    gettingImage();
                } else if (message.getType().equals("updateClientChat")) {
                    answers = (String) oin.readObject();
                    System.out.println("С сервера пришло " + answers.length() + " символов.");
                }
            }

            oos.close();ois.close();//закрываем
            objos.close();oin.close();
        } catch (IOException e) {
            System.out.println("IOEx clientSocket.run()");
        } catch (InterruptedException e) {
            System.out.println("Interrupted clientSocket.run()");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundEx clientSocket.run(): " + e);
        }
    }

    //***ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ***
    private void listening() throws IOException{
        String in = ois.readUTF();
        System.out.println("Сервер ответил по поводу загрузки вопроса/ответа/комментария/обновления номера слайда: " + in);
    }

    private void gettingImage() throws IOException,ClassNotFoundException,InterruptedException{
        message = (Message) oin.readObject();
        System.out.println("Текст полученного Message: "+message.getMessage());
        System.out.println("Картинка сформирована!");
        System.out.println("Сервер послал картинку слайда::: " + SwingFXUtils.toFXImage(message.getImageVision().getImage(),null).impl_getUrl());
    }
    //***


    //геттеры для возвращения полезных данных
    public synchronized void setMessage(Message mes){
        this.message = mes;
    }

    public synchronized Message getMessage(){
        return message;
    }

    public String getAnswers(){
        return answers;
    }
    //

}
