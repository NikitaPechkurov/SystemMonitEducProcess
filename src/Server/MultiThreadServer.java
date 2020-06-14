package Server;

import Model.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MultiThreadServer extends Thread {

    //static ExecutorService executeIt = Executors.newFixedThreadPool(10);//ссылки на соединения
    static ArrayList<Thread> connections = new ArrayList<>(10);
    private ServerSocket server;//тот самый сервер
    private Message message;
    private String TextForServer;
    private String TextForClient;
    private MonoThreadClientHandler handler;

    public MultiThreadServer() throws IOException{
        try {
            server = new ServerSocket(3214);
            message = new Message("1","1","https://i.imgur.com/L0iDcra.png","image");
        }catch(IOException e){
            System.out.println("Exception in initialize MultiThreadServer: "+e);
        }
    }

    @Override
    public void run(){
        // стартуем сервер на порту 3214
        try  {
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
        System.out.println("Сообщение внутри сервера: "+mes.getMessage());
        message = mes;
    }

    public synchronized Message getMessage() {
        return message;
    }

    public Thread getConnection(int n){
        return connections.get(n);
    }

    public synchronized void addTextForServer(String text){
        TextForServer += text;
    }

    public synchronized String getTextForServer(){
        String a = TextForServer;
        TextForServer = "";
        return a;
    }

    public synchronized void addTextForClient(String text){
        TextForClient += text;
    }

    public synchronized String getTextForClient(){
        return TextForClient;
    }
}