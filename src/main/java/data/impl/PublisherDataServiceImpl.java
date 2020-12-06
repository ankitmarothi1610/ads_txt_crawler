package data.impl;

import data.PublisherDataService;
import db.MysqlClientManager;
import helpers.PublisherHelper;
import models.Publisher;

import java.sql.*;
import java.util.List;

public class PublisherDataServiceImpl implements PublisherDataService {
    Connection connection;
    public PublisherDataServiceImpl()  {
    }

    public int bulkUpdatePublishers(List<Publisher> publishersList)  {
        Statement statement = null;
        connection = MysqlClientManager.createConnection();
        try {
            String sql = "INSERT INTO ads.publishers(name,url) VALUES ";

            statement = connection.createStatement();
            for (int i = 0; i < publishersList.size(); i++) {
                StringBuffer sb = new StringBuffer();
                sb.append(sql);
                Publisher publisher = publishersList.get(i);
                sb.append("(\"" + publisher.name + "\",\"" + publisher.url+ "\")");
                sb.append(" ON DUPLICATE KEY UPDATE name = \"" + publisher.name + "\";");
                statement.addBatch(sb.toString());
                if (i%10 == 0)
                    statement.executeBatch();
            }
            System.out.println("Batch for update " + statement.getUpdateCount());
            statement.executeBatch();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        } try {
            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return publishersList.size();
    }

    public ResultSet getIterablePublisherCrawlUrls() {
        String sql = "SELECT id, name, url FROM ads.publishers WHERE processed = false ORDER BY id ASC";
        ResultSet rs = null;
        PreparedStatement stmt;
        System.out.println("Query to get Publisher records " + sql);
        connection = MysqlClientManager.createConnection();
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setFetchSize(PublisherHelper.FETCH_SIZE);
            rs = stmt.executeQuery();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return rs;
    }

    public void markTrue(String url, String field) {
        String sql = "UPDATE ads.publishers set " + field + " = true WHERE url = ?";
        connection = MysqlClientManager.createConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, url);
            System.out.println("Query to mark urls as processed: " + preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            if (preparedStatement != null)
                MysqlClientManager.destroyQueryObjects(preparedStatement, null);
            try {
                connection.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }
}
