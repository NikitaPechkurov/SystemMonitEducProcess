package Client;

import Model.Message;
import javafx.embed.swing.SwingFXUtils;

import java.io.*;
import java.net.Socket;

public class ClientSocket extends Thread{
    /**
     * Класс, служащий обработчиком соединения со стороны клиента.
     * @author Nikita Pechkurov
     * *@version 2.3.3
     */
    private static Socket socket;
    private DataOutputStream oos;
    private DataInputStream ois;
    private ObjectOutputStream objos;
    private ObjectInputStream oin;
    private Message message;//контейнер для Message уже на стороне клиента
    private String answers;
    private boolean flag = false;//защелка для общения с сервером

    public ClientSocket() {
        /**
         * Конструктор класса по умолчанию. Создает новый сокет на порту 3214,
         * инициирует сообщение по умолчанию для запроса первого слайда.
         */
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
        /**
         * Главный метод выполнения в потоке. Начинает выполнение при вызове метода start() у экземпляра.
         */
        try  // создаём объект для записи сообщений в созданный скокет, для
        // чтения сообщений из сокета
        {
            oos = new DataOutputStream(socket.getOutputStream());
            ois = new DataInputStream(socket.getInputStream());
            objos = new ObjectOutputStream(oos);
            oin = new ObjectInputStream(ois);
            System.out.println("Client DOS & DIS initialized");

            while (!socket.isClosed()) {
                if (flag) {
                    objos.writeObject(message);//СНАЧАЛА ЗАПИСЬ
                    objos.flush();
                    System.out.println("ClientSocket: сообщение передано на сервер.");
                    System.out.println("reading...");
                    //ПОТОМ ЧТЕНИЕ!
                    //клиент-сокет может попросить загрузить вопрос, комментарий; послать ему картинку, первую картинку; и обновить клиентский чат
                    if (message.getType().equals("question") || message.getType().equals("commentClient")) {
                        listening();
                    } else if (message.getType().equals("getSlide")) {
                        gettingImage();
                    } else if (message.getType().equals("updateClientChat")) {
                        message = (Message) oin.readObject();
                        System.out.println("С сервера пришло " + message.getMessage().length() + " символов.");
                    }
                    flag = false;//после обработки установили защелку
                }
            }
            oos.close();
            ois.close();//закрываем
            objos.close();
            oin.close();
            socket.close();

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
        /**
         * Метод презназначен для прослушивания канала и получения ответа
         * в текстовом формате. Применяется после отправки данных на сервер.
         */
        String in = ois.readUTF();
        System.out.println("Сервер ответил по поводу загрузки вопроса/ответа/комментария/обновления номера слайда: " + in);
    }

    private void gettingImage() throws IOException,ClassNotFoundException,InterruptedException{
        /**
         * Метод презназначен для получения сообщения и распаковки
         * содержащегося в сообщении слайда.
         */
        message = (Message) oin.readObject();
        System.out.println("Текст полученного Message: "+message.getMessage());
        System.out.println("Картинка сформирована!");
        System.out.println("Сервер послал картинку слайда::: " + SwingFXUtils.toFXImage(message.getImageVision().getImage(),null).impl_getUrl());
    }
    //***

    //геттеры для возвращения полезных данных
    public synchronized void setMessage(Message mes){
        /**
         * Метод для устновки сообщения на клиент-сокет
         */
        this.message = mes;
    }

    public synchronized Message getMessage(){
        /**
         * Метод возвращает сообщение, полученное с сервера
         */
        return message;
    }
    //

    public synchronized void setFlag(boolean flag) {//для установки защелки
        /**
         * Метод, используемый для установки блокировки исполнения
         * потока отправки сообщений на сервер.
         * Принимает логическую переменную в качестве параметра.
         */
        this.flag = flag;
    }

}
