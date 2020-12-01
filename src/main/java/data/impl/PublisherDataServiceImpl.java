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
        Statement statement = null;
        try {
            String sql = "INSERT INTO ads.publishers(name,url) VALUES ";

            statement = connection.createStatement();
            int j = 0;
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
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            MysqlClientManager.destroyQueryObjects(statement, null);
        }
        return publishersList.size();
    }
}
