package data.impl;

import data.PublisherDataService;
import db.MysqlClientManager;
import models.Publisher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
            String sql = "INSERT INTO ads.publishers(name,url) VALUES (?,?) ON DUPLICATE KEY UPDATE url = ?;";
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < publishersList.size(); i++) {
                Publisher publisher = publishersList.get(i);
                ps.setString(1, publisher.name);
                ps.setString(2, publisher.url);
                ps.setString(3, publisher.url);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            MysqlClientManager.destroyQueryObjects(ps, null);
        }
        return publishersList.size();
    }
}
