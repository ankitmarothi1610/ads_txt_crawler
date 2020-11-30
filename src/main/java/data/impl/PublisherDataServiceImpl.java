package data.impl;

import data.PublisherDataService;
import db.MysqlClientManager;
import models.Publisher;

import java.sql.*;
import java.util.List;

public class PublisherDataServiceImpl implements PublisherDataService {
    Connection connection;
    public PublisherDataServiceImpl()  {
        try {
            connection = MysqlClientManager.getConnection();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public int bulkUpdatePublishers(List<Publisher> publishersList)  {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO ads.publishers(name,url) VALUES (?,?) ON DUPLICATE KEY UPDATE url = ?";
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int j = 0;
            for (int i = 0; i < publishersList.size(); i++) {
                Publisher publisher = publishersList.get(i);
                ps.setString(1, publisher.name);
                ps.setString(2, publisher.url);
                ps.setString(3, publisher.url);
                ps.addBatch();
            }
            System.out.println("Batch for update " + ps);
            int[] executeResult = ps.executeBatch();
            ResultSet ids = ps.getGeneratedKeys();
            for (int i = 0; i < executeResult.length; i++) {
                ids.next();
                if (executeResult[i] == 1) {
                    System.out.println("Execute Result: " + i + ", Update Count: " + executeResult[i] + ", id: "
                            + ids.getLong(1));
                }
            }
            ps.clearBatch();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            MysqlClientManager.destroyQueryObjects(ps, null);
        }
        return publishersList.size();
    }
}
