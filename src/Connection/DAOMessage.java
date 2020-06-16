package Connection;

import Model.Message;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAOMessage {
    /**
     * Класс, содержащий методы для работы с данными,
     * полученными из БД.
     * @author Nikita Pechkurov
     * *@version 2.3
     */
    //поиск сообщения по введённому. Вернет экземпляр
    public static ObservableList<Message> searchMessage (String messageSelect) throws SQLException,
            ClassNotFoundException, IOException {
        /**
         * Метод, возвращающий список сообщений из БД, схожих
         * с введенным сообщением. Возвращает список ObservableList<></>
         */
        //Declare a SELECT statement
        String selectStmt = "SELECT * FROM " + DBConnect.DBName + " WHERE "+ DBConnect.nameColMessage.MESSAGE +" LIKE '" + messageSelect + "';";//было = вместо like
        //Execute SELECT statement
        try {
        //Get ResultSet from dbExecuteQuery method
            ResultSet rsMes = DBConnect.dbExecuteQuery(selectStmt);
            //Send ResultSet to the getEmployeeFromResultSet method and get employee object
            ObservableList<Message> messageListFind = FXCollections.observableArrayList();
            while (rsMes.next()) {
                Message mes = getMessageFromResultSet(rsMes);
                messageListFind.add(mes);
            }
            //Return employee object
            return messageListFind;
        } catch (SQLException e) {
            System.out.println("While searching an question with " + messageSelect + " question, an error occurred: " + e
                    + ". Method: searchMessage()");
            //Return exception
            throw e;
        }
    }

    //ищет экземпляр сообщения (для картинок) по типу(image) и номеру слайда
    public static Message searchImageMessage (String id_slide) throws SQLException, ClassNotFoundException, IOException {
        /**
         * Данный метод возвращает сообщение типа 'image', которое содержит
         * ссылку на слайд. В качестве параметров принимает номер слайда.
         * Возвращает экземпляр сообщения.
         */
        //Declare a SELECT statement
        String selectStmt = "SELECT * FROM " + DBConnect.nameColTables.MESSAGES + " WHERE "
                + DBConnect.nameColMessage.TYPE+" = 'image' AND " + DBConnect.nameColMessage.ID_SLIDE + " = '"+ id_slide +"';";
        //Execute SELECT statement
        Message mes = new Message("1","2","3","4");//возвращаемое значение
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet rsMes = DBConnect.dbExecuteQuery(selectStmt);
            while (rsMes.next()) {
                mes = getMessageFromResultSet(rsMes);
            }
            //Return message object
            return mes;
        } catch (SQLException e) {
            System.out.println("While searching an message with " + id_slide + " id_slide, an error occurred: " + e
                    + ". Method: searchImageMessage()");
            //Return exception
            throw e;
        }
    }

    //сформировать экземпляр сообщения из вернувшегося ответа от БД
    private static Message getMessageFromResultSet(ResultSet rs) throws SQLException, IOException {
        /**
         * Данный метод создает и возвращает экземпляр сообщения из полученных
         * данных в виде ResultSet. Принимает в качестве параметра экземпляр ResultSet.
         */
        Message mess = new Message("aaaa", "bbbb", "ll11","cccc");
        mess.setId(rs.getString(DBConnect.nameColMessage.ID));
        mess.setId_user(rs.getString(DBConnect.nameColMessage.ID_USER));
        mess.setId_slide(rs.getString(DBConnect.nameColMessage.ID_SLIDE));
        mess.setMessage(rs.getString(DBConnect.nameColMessage.MESSAGE));//получение по названию столбца в БД
        mess.setType(rs.getString(DBConnect.nameColMessage.TYPE));
        return mess;
    }

    //получить весь список сообщений (вопросов)
    public static String searchListMessage(String id_user, String type) throws SQLException,
            ClassNotFoundException, IOException {
        //Declare a SELECT statement
        String selectStmt = "SELECT * FROM " + DBConnect.nameColTables.MESSAGES + " WHERE "+ DBConnect.nameColMessage.ID_USER +
                " = '" + id_user + "' AND Type = '"+type+"';";
        //Execute SELECT statement
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet rs = DBConnect.dbExecuteQuery(selectStmt);
            //Send ResultSet to the getEmployeeList method and get employee object
            String questions = "";
            while (rs.next()) {
                Message mes = getMessageFromResultSet(rs);
                User us = DAOUser.searchUserFromId(mes.getId_user());
                questions += "Sl:"+mes.getId_slide()+". "+us.getUsername()+": "+mes.getMessage()+", "+mes.getType()+";";
                questions +="\r\n";
            }
            //Return employee object
            return questions;
        } catch (SQLException e) {
            System.out.println("SQL select operation has been failed: " + e + ". Method: searchListMessage()");
            //Return exception
            throw e;
        }
    }

    //удаление из БД сообщения по Id
    public static void deleteMessageWithId (String messId) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement
        String updateStmt =
                "BEGIN\n" +
                        " DELETE FROM " + DBConnect.DBName + "\n" +
                        " WHERE " + DBConnect.nameColMessage.ID + " = " +
                        messId +";\n" +
                        " COMMIT;\n" +
                        "END;";
        //Execute UPDATE operation
        try {
            DBConnect.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e + ". Method: deleteMessageWithId()");
            throw e;
        }
    }

    //вставка данных в БД на основе экзмепляра message. ID - автоинкрементное поле
    public static void insertMessage(Message message) throws SQLException, ClassNotFoundException {
        /**
         * Данный метод предназначен для вставки сообщеня в БД.
         * В качестве параметра принимает сообщение Message.
         * Ничего не возвращает.
         */
        //Declare a DELETE statement
        String updateStmt = "INSERT INTO " + DBConnect.nameColTables.MESSAGES +"("+ DBConnect.nameColMessage.ID_USER+","+
                DBConnect.nameColMessage.ID_SLIDE+","+DBConnect.nameColMessage.MESSAGE +","
                +DBConnect.nameColMessage.TYPE+")"+ " VALUES (" + message.record() + ");";
        //Execute DELETE operation
        try {
            DBConnect.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while INSERT Operation: " + e + ". Method: insertMessage()");
            throw e;
        }
    }

    //обновление строки номера текущего слайда
    public static void updateSlideNumber(String slideNum) throws SQLException,ClassNotFoundException {
        String updateStmt = "UPDATE "+DBConnect.nameColTables.MESSAGES + " SET "+DBConnect.nameColMessage.ID_SLIDE+ " = "+
                slideNum +" WHERE "+DBConnect.nameColMessage.TYPE + " = "+"'currentSlide';";
        try{
            DBConnect.dbExecuteUpdate(updateStmt);
        }catch (SQLException e){
            System.out.print("Error occurred while UPDATE Operation: " + e + ". Method: updateSlideNumber()");
        }
    }

    //поиск изображения текущего слайда с получением значения из БД type = currentSlide
    public static Message searchCurrentMessageForImage() throws SQLException, ClassNotFoundException, IOException {
        String currentNumberOfSlideStmt = "SELECT TOP 1 " + DBConnect.nameColMessage.ID_SLIDE + " FROM " +
                DBConnect.nameColTables.MESSAGES + " WHERE " + DBConnect.nameColMessage.TYPE + " = 'currentSlide';";
        String slideNumber = "";
        try {
            ResultSet rs = DBConnect.dbExecuteQuery(currentNumberOfSlideStmt);
            while (rs.next()) {
                slideNumber = rs.getString(1);
                System.out.println("Найден id_slide: "+slideNumber);
            }
            String selectStmt = "SELECT * FROM " + DBConnect.nameColTables.MESSAGES + " WHERE "
                    + DBConnect.nameColMessage.TYPE + " = 'image' AND "
                    + DBConnect.nameColMessage.ID_SLIDE + " = '" + slideNumber + "';";
            Message ems = new Message("1", "2", "3", "4");//возвращаемое значение
            ResultSet rsMes = DBConnect.dbExecuteQuery(selectStmt);
            while (rsMes.next()) {
                ems = getMessageFromResultSet(rsMes);
            }
            //Return message object
            return ems;
        } catch (SQLException e) {
            System.out.println("While searching an message with " + slideNumber +" id_slide, an error occurred: " + e
                    + ". Method: searchCurrentMessageForImage()");
            //Return exception
            throw e;
        }
    }

}
