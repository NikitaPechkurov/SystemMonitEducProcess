package sample;

import Connection.DBConnect;
import Model.DAOMessage;
import Model.Message;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class MonoThreadClientHandler extends Thread {

    private static Socket clientDialog;//клиент
    DataOutputStream out;
    DataInputStream in;
    ObjectInputStream oin;
    ObjectOutputStream outputStream;
    MultiThreadServer server;

    public MonoThreadClientHandler(Socket client, MultiThreadServer server) {
        clientDialog = client;
        this.server = server;
    }

    @Override
    public void run() {

        try {
            // инициируем каналы общения в сокете, для сервера

            // канал записи в сокет следует инициализировать сначала канал чтения для избежания блокировки выполнения программы на ожидании заголовка в сокете
            out = new DataOutputStream(clientDialog.getOutputStream());

            // канал чтения из сокета
            in = new DataInputStream(clientDialog.getInputStream());
            System.out.println("DataInputStream created");

            System.out.println("DataOutputStream  created");
            // начинаем диалог с подключенным клиентом в цикле, пока сокет не
            // закрыт клиентом
            while (!clientDialog.isClosed()) {
                System.out.println("Server reading from channel");
                oin = new ObjectInputStream(in);
                outputStream = new ObjectOutputStream(out);
                // серверная нить ждёт в канале чтения (inputstream) получения
                // данных клиента после получения данных считывает их
                try {
                    Message entry_message = (Message) oin.readObject();
                    // и выводит в консоль
                    System.out.println("READ from clientDialog message - " + entry_message.record());
                    Thread.sleep(5000);
                    if (entry_message.getType().equals("question") ||
                            entry_message.getType().equals("comment")) {
                        insertingMessageToDB(entry_message);
                    }

                    /*else if(entry_message.getType().equals("getImage")){//получение картинки слайда для окна лектора
                        outputStream.writeObject(DAOMessage.searchImageMessage(entry_message.getId_slide()));
                        outputStream.flush();
                        System.out.println("Message с картинкой вернули на админский clientSocket!");
                    }*/

                    else if (entry_message.getType().equals("getSlide")) {//получение текущей картинки слайда при подключении клиента
                        outputStream.writeObject(server.getMessage());
                        System.out.println("Возвращаем такую картинку: "+server.getMessage().getMessage());
                        outputStream.flush();
                        System.out.println("Message с картинкой слайда вернули на clientSocket!");
                    } else if (entry_message.getType().equals("updateClientChat")) {
                        String answers = DAOMessage.searchListMessage("1", "answer");
                        answers += DAOMessage.searchListMessage("1", "comment");
                        outputStream.writeObject(answers);
                        outputStream.flush();
                        System.out.println("Сервер передал строку answers на клиент-сокет!");
                    }

                    /*else if(entry_message.getType().equals("currentSlide")){
                        DAOMessage.updateSlideNumber(entry_message);
                        System.out.println("Server try writing to channel");
                        out.writeUTF("Значение текущего слайда обновлено в БД!");
                        System.out.println("Server Wrote message to clientDialog.");
                    }*/

                    else System.out.println("********Непонятный тип сообщения Message*******");
                }catch(ClassNotFoundException ec){
                    System.out.println("ClassNotF: " + ec);
                }catch(SQLException e) {
                    System.out.println("SQLException: " + e);
                }

                // освобождаем буфер сетевых сообщений
                out.flush();
                oin.close();//закрываем именно ObjectInputStream
                // возвращаемся в началло для считывания нового сообщения
            }

            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            // после цикла проверки отключения пользователя
            // закрываем сначала каналы сокета !
            in.close();
            out.close();
            // потом закрываем сокет общения с клиентом в нити моносервера
            clientDialog.close();
            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }

    private void insertingMessageToDB(Message entry) throws IOException, SQLException, ClassNotFoundException{
        //*******ДОБАВКА СООБЩЕНИЯ В БД
        DAOMessage.insertMessage(entry);
        //и выводим в консоль
        System.out.println("Server try writing to channel");
        out.writeUTF("Сообщение было записано в БД!");
        System.out.println("Server Wrote message to clientDialog.");
    }

}