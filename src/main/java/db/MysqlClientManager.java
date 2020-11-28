package db;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class MysqlClientManager {
    private static DataSource ds;
    private static String baseUrl = "jdbc:mysql://localhost/";
    private static String database = "ads";
    private static String userName = "root";
    private static String password = "ankiT@123";
    private static int MIN_IDLE_CONNECTIONS = 5;
    private static int MAX_IDLE_CONNECTIONS = 10;
    private static int MAX_PREPARED_STATEMENTS = 500;

    private static String getConnectionUrl() {
        StringBuilder connectionStr = new StringBuilder();
        connectionStr.append(baseUrl);
        connectionStr.append(database).append("?");
        connectionStr.append("user=").append(userName).append("&");
        connectionStr.append("password=").append(password);
        return connectionStr.toString();
    }

    public static Connection createConnection() {
        Connection connection = null;

        System.out.println("The connection string is " + getConnectionUrl());
        try {
            connection = DriverManager.getConnection(getConnectionUrl());
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return connection;
    }

    private static BasicDataSource createConnectionPool() {
        BasicDataSource ds = new BasicDataSource();
        if (ds == null) {
            ds.setUrl(baseUrl);
            ds.setUsername(userName);
            ds.setPassword(password);

            ds.setMinIdle(MIN_IDLE_CONNECTIONS);
            ds.setMaxIdle(MAX_IDLE_CONNECTIONS);
            ds.setMaxOpenPreparedStatements(MAX_PREPARED_STATEMENTS);
        }
        return  ds;
    }

    public static Connection getConnection() throws SQLException {
        return createConnectionPool().getConnection();
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
