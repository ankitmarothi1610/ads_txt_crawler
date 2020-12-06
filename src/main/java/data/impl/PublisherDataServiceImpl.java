package data.impl;

import data.PublisherDataService;
import db.MysqlClientManager;
import helpers.PublisherHelper;
import models.Publisher;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublisherDataServiceImpl implements PublisherDataService {
    public PublisherDataServiceImpl()  {}

    public int bulkUpdatePublishers(List<Publisher> publishersList)  {
        Statement statement = null;
        Connection con = null;
        try {
            String sql = "INSERT INTO ads.publishers(name,url) VALUES ";
            con = MysqlClientManager.createConnection();
            statement = con.createStatement();
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
            MysqlClientManager.destroyQueryObjects(con, null, null);
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        return publishersList.size();
    }

    public ResultSet getIterablePublisherCrawlUrls() {
        String sql = "SELECT id, name, url FROM ads.publishers WHERE processed = false ORDER BY id ASC";
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection con = null;
        System.out.println("Query to get Publisher records " + sql);
        try {
            con = MysqlClientManager.createConnection();
            stmt = con.prepareStatement(sql);
            stmt.setFetchSize(PublisherHelper.FETCH_SIZE);
            rs = stmt.executeQuery();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return rs;
    }

    public void markTrue(String url, String field) {
        String sql = "UPDATE ads.publishers set " + field + " = true WHERE url = ?";
        PreparedStatement preparedStatement = null;
        Connection con = null;
        try {
            con = MysqlClientManager.createConnection();
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, url);
            System.out.println("Query to mark urls as processed: " + preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            MysqlClientManager.destroyQueryObjects(con, preparedStatement, null);
        }
    }
    public void setFlagsToFalse() {
        String sql = "UPDATE ads.publishers set processed = false and notFound = false";
        PreparedStatement ps = null;
        Connection con = null;
        try {
            con = MysqlClientManager.createConnection();
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            MysqlClientManager.destroyQueryObjects(con, ps, null);
        }
    }

    public Map<String, Integer> getMaxAndMinIds() {
        String sql = "select max(id) as max, min(id) as min from ads.publishers";
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs = null;
        Map<String, Integer> result = null;
        try {
            con = MysqlClientManager.createConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = new HashMap<>(2);
                result.put("max", rs.getInt(1));
                result.put("min", rs.getInt(2));
            }
        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            MysqlClientManager.destroyQueryObjects(con, ps, rs);
        }
        return result;
    }

    public List<Publisher> getRecordsBetweenIds(int min, int max) {
        String sql = "select id, name, url from ads.publishers where id between " + min + " and " + max;
        System.out.println("Query to get records: " + sql);
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs = null;
        List<Publisher> publisherList = null;
        try {
            publisherList = new ArrayList<>();
            con = MysqlClientManager.createConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Publisher publisher = new Publisher();
                publisher.id = rs.getInt(1);
                publisher.name = rs.getString(2);
                publisher.url = rs.getString(3);
                publisherList.add(publisher);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            MysqlClientManager.destroyQueryObjects(con, ps, rs);
        }
        return publisherList;
    }
}
