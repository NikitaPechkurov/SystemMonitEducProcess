package Server;

import Model.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer extends Thread {

    //static ExecutorService executeIt = Executors.newFixedThreadPool(10);//ссылки на соединения
    static ArrayList<Thread> connections = new ArrayList<>(10);
    private ServerSocket server;//тот самый сервер
    private Message message;
    private String TextForServer;
    private String TextForClient;

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
            while (!server.isClosed()) {

                // подключения к сокету общения под именем - "clientDialog" на
                // серверной стороне
                Socket client = server.accept();
                // после получения запроса на подключение сервер создаёт сокет
                // для общения с клиентом и отправляет его в отдельную нить
                // в Runnable(при необходимости можно создать Callable)
                // монопоточную нить = сервер - MonoThreadClientHandler и тот
                // продолжает общение от лица сервера
                connections.add(new MonoThreadClientHandler(client,this));
                System.out.print("Connection accepted.");
                //стремиться к прохождению по списку и передать image
                //executeIt.shutdownNow().get(1).run();//обратиться к каждому MonoThread
            }

            // закрытие пула нитей после завершения работы всех нитей
            for (int i = 0;i < connections.size(); i++){// = executeIt.shutdown();
                connections.get(i).interrupt();
            }
            System.out.println("All connections are closed!");
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

    public void addTextForServer(String text){
        TextForServer += text;
    }

    public String getTextForServer(){
        return TextForServer;
    }

    public void addTextForClient(String text){
        TextForClient += text;
    }

    public String getTextForClient(){
        return TextForClient;
    }
}