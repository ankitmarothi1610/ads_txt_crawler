import db.MysqlClientManager;
import services.PublisherService;
import services.impl.PublisherServiceImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {
    public void tesDBConnection() {
        MysqlClientManager client = new MysqlClientManager();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
          connection = client.getConnection();
          stmt = connection.createStatement();
          rs = stmt.executeQuery("select * from ads.publishers;");
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
                System.out.println("Failed connecting to db");
                sqe.printStackTrace();
                System.exit(0);
            }
        }
    }

    public static void main(String args[]) {
        Application app = new Application();
        app.tesDBConnection();
        PublisherService publisherService = new PublisherServiceImpl();
        publisherService.addPublishersFromFile("src/main/resources/publishers.txt");
        System.exit(0);
    }
}
