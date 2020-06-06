package Server;

import Connection.DAOMessage;
import Connection.DAOUser;
import Model.Message;
import javafx.embed.swing.SwingFXUtils;

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
        this.start();
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
            System.out.println("DataOutputStream created");

            // начинаем диалог с подключенным клиентом в цикле, пока сокет не
            // закрыт клиентом
            while (!clientDialog.isClosed()) {
                System.out.println("MultiThreadServer reading from channel");
                oin = new ObjectInputStream(in);
                outputStream = new ObjectOutputStream(out);
                System.out.println("Object -input -output Stream created");
                // серверная нить ждёт в канале чтения (inputstream) получения
                // данных клиента после получения данных считывает их
                try {
                    //СНАЧАЛА ПРОЧИТАЛИ
                    Message entry_message = (Message) oin.readObject();
                    // и выводим в консоль
                    System.out.println("READ from clientDialog message - " + entry_message.record());
                    Thread.sleep(5000);

                    //ПОТОМ ЗАПИСАЛИ!
                    if (entry_message.getType().equals("question") ||
                            entry_message.getType().equals("commentClient")) {
                        server.setMessage(entry_message);//?
                        server.addTextForServer("Sl: "+entry_message.getId_slide()+", user: "+
                        DAOUser.searchUserFromId(entry_message.getId_user()).getUsername()+". "+entry_message.getType()+
                                ": "+entry_message.getMessage()+"\r\n");
                        out.writeUTF("Handler передал вопрос/комментарий серверу.");
                    }

                    /*else if(entry_message.getType().equals("getImage")){//получение картинки слайда для окна лектора
                        outputStream.writeObject(DAOMessage.searchImageMessage(entry_message.getId_slide()));
                        outputStream.flush();
                        System.out.println("Message с картинкой вернули на админский clientSocket!");
                    }*/

                    else if (entry_message.getType().equals("getSlide")) {//получение текущей картинки слайда при подключении клиента
                        Message toClient = server.getMessage();
                        outputStream.writeObject(toClient);
                        System.out.println("Возвращаем картинку...");
                        outputStream.flush();
                        System.out.println("Message с такой картинкой слайда вернули на clientSocket!: "+ SwingFXUtils.toFXImage(toClient.getImageVision().getImage(),null).impl_getUrl());
                    } else if (entry_message.getType().equals("updateClientChat")) {
                        String answers = server.getTextForClient();
                        outputStream.writeObject(answers);
                        outputStream.flush();
                        System.out.println("Сервер передал строку answers на клиент-сокет!");
                    }

                    /*else if(entry_message.getType().equals("currentSlide")){
                        DAOMessage.updateSlideNumber(entry_message);
                        System.out.println("MultiThreadServer try writing to channel");
                        out.writeUTF("Значение текущего слайда обновлено в БД!");
                        System.out.println("MultiThreadServer Wrote message to clientDialog.");
                    }*/

                    else System.out.println("********Непонятный тип сообщения Message*******");
                }catch(ClassNotFoundException ec){
                    System.out.println("ClassNotF: " + ec);
                }catch(SQLException e) {
                    System.out.println("SQLException: " + e);
                }

                // освобождаем буфер сетевых сообщений
                out.flush();
                //oin.close();//закрываем именно ObjectInputStream
                //outputStream.close();
                // возвращаемся в началло для считывания нового сообщения
            }

            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            // после цикла проверки отключения пользователя
            // закрываем сначала каналы сокета !
            in.close();
            out.close();
            //oin.close();//закрываем именно ObjectInputStream
            //outputStream.close();
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

}