package Connection;

import Model.Message;
import com.sun.rowset.CachedRowSetImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.*;

public class DBConnect {
    /**
     * Класс для установления соединения между проектом и базой данных
     * Свойства класса - параметры подключения к БД.
     */
    private static final String DB_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String DB_URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:1890;databaseName=Uspevaemost;user=sa;password=12345";
    //private static final String DB_URL = "jdbc:sqlserver://localhost:1890;"+"databaseName=Uspevaemost;integratedSecurity=true";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "12345";
    public static final String DBName = "Uspevaemost";

    public static final class nameColTables{
        public static final String MESSAGES = "Messages";
        public static final String USERS = "Users";
    }

    public static final class nameColMessage {
        public static final String ID = "id";
        public static final String ID_USER = "id_user";
        public static final String ID_SLIDE = "id_slide";
        public static final String MESSAGE = "Message";
        public static final String TYPE = "Type";
    }

    public static final class nameColUser{
        public static final String ID = "id";
        public static final String USERNAME = "Username";
        public static final String PASSWORD = "Password";
        public static final String POST = "Post";
    }


    //создание соединения
    private static Connection getDBConnection() {
        /**
         * Метод, осуществляющий подключение к БД
         * Выдает ошибку, если подключение невозможно.
         */
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("\r\nОшибка: " + e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database CONNECTED!");
            return dbConnection;
        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage() + ". Method: getDBConnection()");
        }
        return dbConnection;
    }


    //createDbUserTable() - База Данных уже была создана


    //простой запрос к БД.  Вернет cachedResultSet
    public static ResultSet dbExecuteQuery(String queryStmt) throws SQLException, ClassNotFoundException {
        /**
         * Метод, предназначенный для выполнения SQL-запроса к базе данных
         * Возвращает кэшированные данные в виде объекта ResultSer.
         */
        //Declare statement, resultSet and CachedResultSet as null
        Connection dbConnection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        CachedRowSetImpl crs = null;
        try {
            //Connect to DB (Establish Oracle Connection)
            dbConnection = getDBConnection();
            System.out.println("Select statement: " + queryStmt + "\n");
            //Create statement
            statement = dbConnection.createStatement();
            //Execute select (query) operation
            resultSet = statement.executeQuery(queryStmt);
            //CachedRowSet Implementation
            //In order to prevent "java.sql.SQLRecoverableException:Closed Connection: next" error
            //We are using CachedRowSet
            crs = new CachedRowSetImpl();
            crs.populate(resultSet);
        } catch (SQLException e) {
            System.out.println("Problem occurred at executeQuery operation : " + e + ". Method: dbExecuteQuery()");
            throw e;
        } finally { //закрыли соединения и данные
            if (resultSet != null) {
                //Close resultSet
                resultSet.close();
            }
            if (statement != null) {
                //Close Statement
                statement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
        //Return CachedRowSet
        return crs;
    }

    //обновление БД. Ничего не вернет
    public static void dbExecuteUpdate(String sqlStmt) throws SQLException, ClassNotFoundException {
        /**
         * Метод для выполнения запросов на обновление базы данных.
         * Ничего не возвращает.
         */
        //Declare statement as null
        Connection dbConnection = null;
        Statement statement = null;
        try {
            //Connect to DB (Establish Oracle Connection)
            dbConnection = getDBConnection();
            //Create Statement
            statement = dbConnection.createStatement();
            //Run executeUpdate operation with given sql statement
            statement.executeUpdate(sqlStmt);
        } catch (SQLException e) {
            System.out.println("Problem occurred at executeUpdate operation : " + e + ". Method: dbExecuteUpdate()");
            throw e;
        } finally {
            if (statement != null) {
                //Close statement
                statement.close();
            }
            //Close connection
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
    }

    //данный метод заполняет массив ответов с помощью переданного sql-запроса
    //переделать этот метод под сообщения в БД (кто отправил еще поле)
    public ObservableList<Message> createMessages(String query) throws SQLException, IOException {

        ObservableList<Message> messages = FXCollections.observableArrayList();
        Connection dbConnection = null;
        Statement statement = null;
        // String selectTableSQL = "SELECT USER_ID, USERNAME from DBUSER";
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.createStatement();
            // выбираем данные с БД
            ResultSet rs = statement.executeQuery(query);
            // И, если что то было получено, то цикл while сработает
            while (rs.next()) {
                Message message = new Message(rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5));
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println("Ошиюка: " + e.getMessage() + ". Method: create()");
        }
        return messages;
    }

    //данный метод обновляет данные в БД. Использовать после каждого добавления нового сообщения в массив
    public void update(ObservableList<Message> messages) {

        Connection dbConnection = null;
        Statement statement = null;
        // String selectTableSQL = "SELECT USER_ID, USERNAME from DBUSER";
        if (messages.isEmpty()) return;
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.createStatement();
            statement.executeUpdate("INSERT INTO " +
                    nameColTables.MESSAGES + "VALUES"+ messages.get(messages.size()-1).getId()+ messages.get(messages.size()-
                    1).getId_user()+messages.get(messages.size()-1).getId_slide()+messages.get(messages.size()-1).getMessage()
                    + messages.get(messages.size()- 1).getType());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }


}
