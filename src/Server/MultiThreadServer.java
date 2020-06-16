package Server;

import Model.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MultiThreadServer extends Thread {
    /**
     * MultiThreadServer - основной класс-сервер.
     * Предназначен для хранения и обработки подключений,
     * хранения текущего сообщения и текстов форм.
     * @author Nikita Pechkurov
     * *@version 2.3.3
     */
    static ArrayList<Thread> connections = new ArrayList<>(10);
    private ServerSocket server;//тот самый сервер
    private Message message;
    private String TextForServer;
    private String TextForClient;
    private MonoThreadClientHandler handler;

    public MultiThreadServer() throws IOException{
        /**
         * Конструктор по умолчанию. Вызывается при создании нового экзмепляра объекта.
         */
        try {
            server = new ServerSocket(3214);
            message = new Message("1","1","https://i.imgur.com/L0iDcra.png","image");
        }catch(IOException e){
            System.out.println("Exception in initialize MultiThreadServer: "+e);
        }
    }

    @Override
    public void run(){
        /**
         * Главный метод выполнения в потоке. Начинает выполнение при вызове метода start() у экземпляра.
         */
        try  {// стартуем сервер на порту 3214
            System.out.println("Server socket created!");

            // стартуем цикл при условии что серверный сокет не закрыт
            //while (!server.isClosed()) {
            while (true) {
                // подключения к сокету общения под именем - "clientDialog" на
                // серверной стороне
                Socket client = server.accept();
                // после получения запроса на подключение сервер создаёт сокет
                // для общения с клиентом и отправляет его в отдельную нить
                // в Runnable(при необходимости можно создать Callable)
                // монопоточную нить = сервер - MonoThreadClientHandler и тот
                // продолжает общение от лица сервера
                handler = new MonoThreadClientHandler(client,this);
                connections.add(handler);
                handler.start();
                System.out.print("Connection accepted.");
            }
            // закрытие пула нитей после завершения работы всех нитей
            /*for (int i = 0;i < connections.size(); i++){// = executeIt.shutdown();
                connections.get(i).interrupt();
            }*/
            //server.close();
            //System.out.println("All connections are closed!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setMessage(Message mes){
        /**
         * Метод устанавливает сообщение на сервер.
         */
        System.out.println("Сообщение внутри сервера: "+mes.getMessage());
        message = mes;
    }

    public synchronized Message getMessage() {
        /**
         * Метод возвращает текущиее сообщение с сервера.
         */
        return message;
    }

    public Thread getConnection(int n){
        /**
         * Метод для возврата подключения по порядковому номеру
         */
        return connections.get(n);
    }

    public synchronized void addTextForServer(String text){
        /**
         * Данный метод добавляет текст на сервер
         */
        TextForServer += text;
    }

    public synchronized String getTextForServer(){
        /**
         * Данный метод возвращает текущий текст чата для сервера
         */
        String a = TextForServer;
        TextForServer = "";
        return a;
    }

    public synchronized void addTextForClient(String text){
        /**
         * Данный метод добавляет текст для клиентского чата на хранение на сервер
         */
        TextForClient += text;
    }

    public synchronized String getTextForClient(){
        /**
         * Метод для возврата хранимого текста для клиентского чата
         */
        return TextForClient;
    }

    public synchronized void nullTextForClient(){
        TextForClient = null;
    }
}