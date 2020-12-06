import db.MysqlClientManager;
import services.crawler.AdvertiserService;
import services.crawler.impl.AdvertiserServiceImpl;
import services.publisher.PublisherService;
import services.publisher.impl.PublisherServiceImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

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
        System.out.println("Please select from one of the actions");
        System.out.println("1. Test Connection");
        System.out.println("2. Source publishers");
        System.out.println("3. Source Ads.Txt from publishers");
        System.out.println("Please enter 1 / 2 / 3 for the inputs");
        Scanner scanner = new Scanner(System.in);
        int input = 0;
        try {
            input = scanner.nextInt();
        } catch (InputMismatchException inputMismatchException) {
            System.out.println("Input has to be a number");
        }
        Application app = new Application();
        switch(input) {
            case 1:
                app.tesDBConnection();
                break;
            case 2:
                PublisherService publisherService = new PublisherServiceImpl();
                publisherService.addPublishersFromFile("src/main/resources/publishers.txt");
                break;
            case 3:
                AdvertiserService advertiserService = new AdvertiserServiceImpl();
                advertiserService.sourceAdsTxtForPublisherUrls();
                break;
            default:
                System.out.println("Invalid Input");
        }
        System.exit(0);
    }
}
