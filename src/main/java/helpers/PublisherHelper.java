package helpers;

import models.Publisher;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PublisherHelper {
    public static String createPublisherInsertQuery(Publisher publisher) {
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT into ads.publisher(name,url) values (")
                .append(publisher.name)
                .append(",")
                .append(publisher.url)
                .append(");");
        return sb.toString();
    }

    public static void destroyQueryObjects(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
