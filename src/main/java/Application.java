import data.MysqlClientManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {
    public void tesDBConnection() {
        MysqlClientManager client = new MysqlClientManager();
        Connection connection = client.createConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
          stmt = connection.createStatement();
          rs = stmt.executeQuery("select * from publishers;");
          System.out.print("Successfully connected to the db");
        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception sqe) {
                sqe.printStackTrace();;
            }
        }
    }
    public static void main(String args[]) {
        Application app = new Application();
        app.tesDBConnection();
    }
}
