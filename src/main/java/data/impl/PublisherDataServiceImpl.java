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
        try {
            connection = MysqlClientManager.getConnection();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public int bulkUpdatePublishers(List<Publisher> publishersList)  {
        Statement statement = null;
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
        } finally {
            MysqlClientManager.destroyQueryObjects(statement, null);
        }
        return publishersList.size();
    }

    public ResultSet getIterablePublisherCrawlUrls() {
        String sql = "SELECT url FROM ads.publishers WHERE processed = false ORDER BY id ASC";
        ResultSet rs = null;
        PreparedStatement stmt;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setFetchSize(PublisherHelper.FETCH_SIZE);
            rs = stmt.executeQuery();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return rs;
    }

    public void markProcessed(List<String> urls) {
        String sql = "UPDATE ads.publishers set processed = true WHERE url = ?";
        PreparedStatement preparedStatement;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            for (String url: urls) {
                preparedStatement.setString(1, url);
                preparedStatement.addBatch();
            }
            System.out.println("Query to mark urls as processed: " + preparedStatement);
            preparedStatement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
