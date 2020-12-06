package data.impl;

import data.AdvertiserDataService;
import db.MysqlClientManager;
import models.Advertiser;
import models.Publisher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class AdvertiserDataServiceImpl implements AdvertiserDataService {
    Connection connection;
    public AdvertiserDataServiceImpl() {
        try {
            connection = MysqlClientManager.getConnection();
        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void addAdvertiserBatch(List<Advertiser> advertiserList) {
        Statement statement = null;
        String sql = "INSERT INTO ads.advertisers (name, publisherId, accountType, advertiserId, tagId) VALUES";
        try {
            statement = connection.createStatement();
            for (int i = 0; i < advertiserList.size(); i++) {
                StringBuffer sb = new StringBuffer();
                sb.append(sql);
                Advertiser advertiser = advertiserList.get(i);
                sb.append("(\"")
                        .append(advertiser.name)
                        .append("\",\"")
                        .append(advertiser.publisherId)
                        .append("\",\"")
                        .append(advertiser.accountType.toUpperCase())
                        .append("\",\"")
                        .append(advertiser.advertiserId)
                        .append("\",\"")
                        .append(advertiser.tag)
                        .append("\")");
                statement.addBatch(sb.toString());
                if (i%10 == 0) {
                    statement.executeBatch();
                }
            }
            statement.executeBatch();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            MysqlClientManager.destroyQueryObjects(statement, null);
        }
    }

    public void deleteAdsDataForPublisher(Publisher publisher) {
        String sql = "DELETE from ads.advertisers where publisherId = " + publisher.id;
        PreparedStatement ps = null;
        System.out.println("Deleting previous record for publisher " + publisher.url);
        try {
            ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            MysqlClientManager.destroyQueryObjects(ps, null);
        }
    }
}
