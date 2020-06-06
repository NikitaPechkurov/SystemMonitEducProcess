package Connection;

import Connection.DBConnect;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DAOUser {

    //поиск пользователя по введённому username. Вернет экземпляры user
    public static ObservableList<User> searchUser (String usernameSelect) throws SQLException, ClassNotFoundException {
        //Declare a SELECT statement
        String selectStmt = "SELECT * FROM " + DBConnect.nameColTables.USERS + " WHERE "+ DBConnect.nameColUser.USERNAME +" LIKE '" + usernameSelect + "';";//было = вместо like
        //Execute SELECT statement
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet rsUs = DBConnect.dbExecuteQuery(selectStmt);
            //Send ResultSet to the getEmployeeFromResultSet method and get employee object
            ObservableList<User> userListFind = FXCollections.observableArrayList();
            while (rsUs.next()) {
                User user = getUserFromResultSet(rsUs);
                userListFind.add(user);
            }
            //Return employee object
            return userListFind;
        } catch (SQLException e) {
            System.out.println("While searching an question with " + usernameSelect + " question, an error occurred: " + e
                    + ". Method: searchUser()");
            //Return exception
            throw e;
        }
    }


    //поиск пользователя по id_user из message
    public static User searchUserFromId (String id_user) throws SQLException, ClassNotFoundException {
        //Declare a SELECT statement
        String selectStmt = "SELECT * FROM " + DBConnect.nameColTables.USERS + " WHERE "+ DBConnect.nameColUser.ID +" = " + id_user + ";";//было = вместо like
        //Execute SELECT statement
        User user = new User("","","","");
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet rsUs = DBConnect.dbExecuteQuery(selectStmt);
            //Send ResultSet to the getEmployeeFromResultSet method and get employee object
            ObservableList<User> userListFind = FXCollections.observableArrayList();
            while (rsUs.next()) {
                user = getUserFromResultSet(rsUs);
            }
            //Return employee object
            return user;
        } catch (SQLException e) {
            System.out.println("While searching an user with " + id_user + " id_user, an error occurred: " + e
                    + ". Method: searchUser()");
            //Return exception
            throw e;
        }
    }

    //сформировать экземпляр сообщения из вернувшегося ответа от БД
    private static User getUserFromResultSet(ResultSet rs) throws SQLException {
        User us = new User("aaaa", "bbbb", "ll1","cccc");
        us.setId(rs.getString(DBConnect.nameColUser.ID));
        us.setUsername(rs.getString(DBConnect.nameColUser.USERNAME));
        us.setPassword(rs.getString(DBConnect.nameColUser.PASSWORD));//получение по столбцу в БД
        us.setPost(rs.getString(DBConnect.nameColUser.POST));
        return us;
    }

    //получить весь список пользователей
    public static ObservableList<User> searchListUser() throws SQLException, ClassNotFoundException {
        //Declare a SELECT statement
        String selectStmt = "SELECT * FROM " + DBConnect.DBName + ";";
        //Execute SELECT statement
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet rs = DBConnect.dbExecuteQuery(selectStmt);
            //Send ResultSet to the getEmployeeList method and get employee object
            ObservableList<User> usersList = FXCollections.observableArrayList();
            while (rs.next()) {
                User us = getUserFromResultSet(rs);
                usersList.add(us);
            }
            //Return employee object
            return usersList;
        } catch (SQLException e) {
            System.out.println("SQL select operation has been failed: " + e + ". Method: searchListUser()");
            //Return exception
            throw e;
        }
    }

    //удаление из БД пользователя по Id
    public static void deleteUserWithId (String usId) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement
        String updateStmt =
                "BEGIN\n" +
                        " DELETE FROM " + DBConnect.DBName + "\n" +
                        " WHERE " + DBConnect.nameColUser.ID + " = " +
                        usId +";\n" +
                        " COMMIT;\n" +
                        "END;";
        //Execute UPDATE operation
        try {
            DBConnect.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e + ". Method: deleteUserWithId()");
            throw e;
        }
    }

    //удаление из БД пользователя по Username
    public static void deleteUserWithUsername (String userUsername) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement
        String updateStmt =
                "BEGIN\n" +
                        " DELETE FROM " + DBConnect.DBName + "\n" +
                        " WHERE " + DBConnect.nameColUser.USERNAME + " = " +
                        userUsername +";\n" +
                        " COMMIT;\n" +
                        "END;";
        //Execute UPDATE operation
        try {
            DBConnect.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e + ". Method: deleteUserWithUsername()");
            throw e;
        }
    }

    //вставка данных в БД на основе экзмепляра user. ID - автоинкрементное поле
    public static void insertUser(User user) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement
        String updateStmt = "INSERT INTO " + DBConnect.DBName + DBConnect.nameColUser.USERNAME+", "+
                DBConnect.nameColUser.PASSWORD+", "+DBConnect.nameColUser.POST + " VALUES (" + user.record() + ");";
        //Execute DELETE operation
        try {
            DBConnect.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while INSERT Operation: " + e + ". Method: insertUser()");
            throw e;
        }
    }


}