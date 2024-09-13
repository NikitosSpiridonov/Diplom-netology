package dataHelper;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.*;

public class SqlHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SqlHelper() {
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(System.getProperty("db.url"), System.getProperty("db.user"), System.getProperty("db.password"));
    }

    @SneakyThrows(SQLException.class)
    public static void cleanDB() {
        Connection connect = getConnection(); {
            QUERY_RUNNER.execute(connect, "DELETE FROM credit_request_entity");
            QUERY_RUNNER.execute(connect, "DELETE FROM payment_entity");
            QUERY_RUNNER.execute(connect, "DELETE FROM order_entity ");
        }
    }

    @SneakyThrows(SQLException.class)
    public static String getStatusPaymentEntity() {
        Connection connect = getConnection();
        String queryStatus = "SELECT status FROM payment_entity";
        return QUERY_RUNNER.query(connect, queryStatus, new ScalarHandler<>());
    }

    @SneakyThrows(SQLException.class)
    public static String getStatusCreditEntity() {

        Connection connect = getConnection();
        String queryStatus = "SELECT status FROM credit_request_entity";
        return QUERY_RUNNER.query(connect, queryStatus, new ScalarHandler<>());
    }
}