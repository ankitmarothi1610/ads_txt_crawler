package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlClientManager {
    public Connection createConnection() {
        Connection connection = null;
        String baseurl = "jdbc:mysql://localhost/";
        String database = "ads";
        String userName = "root";
        String password = "ankiT@123";
        StringBuilder connectionStr = new StringBuilder();
        connectionStr.append(baseurl);
        connectionStr.append(database).append("?");
        connectionStr.append("user=").append(userName).append("&");
        connectionStr.append("password=").append(password);
        System.out.println("The connection string is " + connectionStr.toString());
        try {
            connection = DriverManager.getConnection(connectionStr.toString());
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return connection;
    }
}
