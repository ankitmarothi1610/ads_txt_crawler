package data.impl;

import data.AdvertiserDataService;
import db.MysqlClientManager;
import models.Advertiser;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class AdvertiserDataServiceImpl implements AdvertiserDataService {
    Connection connection;
    public AdvertiserDataServiceImpl() {
            connection = MysqlClientManager.createConnection();
    }

    @Override
    public void addAdvertiserBatch(List<Advertiser> advertiserList) {
        Statement statement = null;
        String sql = "INSERT INTO ads.advertisers (name, accountType, advertiserId, tagId) VALUES";
        try {
            statement = connection.createStatement();
            for (int i = 0; i < advertiserList.size(); i++) {
                StringBuffer sb = new StringBuffer();
                sb.append(sql);
                Advertiser advertiser = advertiserList.get(i);
                sb.append("(\"")
                        .append(advertiser.name)
                        .append("\",\"")
                        .append(advertiser.accountType)
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
            try {
                connection.close();
            } catch(SQLException sqlException) {
                sqlException.printStackTrace();
            }
            MysqlClientManager.destroyQueryObjects(statement, null);
        }
    }

}
