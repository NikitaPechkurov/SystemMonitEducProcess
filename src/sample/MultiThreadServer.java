package sample;

import Model.Message;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer extends Thread{

    static ExecutorService executeIt = Executors.newFixedThreadPool(10);//ссылки на соединения
    private ServerSocket server;//сервер
    private Message message = new Message("1","1","https://i.imgur.com/L0iDcra.png","image");

    public MultiThreadServer(){
        try {
            server = new ServerSocket(3214);
        }catch(IOException e){
            System.out.println("Exception in initialize MultiThreadServer: "+e);
        }
    }

    @Override
    public void run(){
        // стартуем сервер на порту 3214 и инициализируем переменную для обработки консольных команд с самого сервера
        try (
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Server socket created, command console reader for listen to server commands");

            // стартуем цикл при условии что серверный сокет не закрыт
            while (!server.isClosed()) {

                // проверяем поступившие комманды из консоли сервера если такие
                // были
                if (br.ready()) {
                    System.out.println("Main Server found any messages in channel, let's look at them.");

                    // если команда - quit то инициализируем закрытие сервера и
                    // выход из цикла раздачии нитей монопоточных серверов
                    String serverCommand = br.readLine();
                    if (serverCommand.equalsIgnoreCase("quit")) {
                        System.out.println("Main Server initiate exiting...");
                        server.close();
                        break;
                    }
                }
                // если комманд от сервера нет то становимся в ожидание
                // подключения к сокету общения под именем - "clientDialog" на
                // серверной стороне
                Socket client = server.accept();
                // после получения запроса на подключение сервер создаёт сокет
                // для общения с клиентом и отправляет его в отдельную нить
                // в Runnable(при необходимости можно создать Callable)
                // монопоточную нить = сервер - MonoThreadClientHandler и тот
                // продолжает общение от лица сервера
                executeIt.execute(new MonoThreadClientHandler(client,this));
                System.out.print("Connection accepted.");
                //стремиться к прохождению по списку и передать image
                //executeIt.shutdownNow().get(1).run();//обратиться к каждому MonoThread
            }

            // закрытие пула нитей после завершения работы всех нитей
            executeIt.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMessage(Message mes){
        System.out.println("Сообщение внутри сервера: "+mes.getMessage());
        message = mes;
    }

    public synchronized Message getMessage(){
        return message;
    }
}