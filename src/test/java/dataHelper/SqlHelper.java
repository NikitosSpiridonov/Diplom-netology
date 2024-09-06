package dataHelper;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.*;

public class SqlHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SqlHelper() {
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(System.getProperty("db.url"), "app", "pass");
    }

    static final String notFound = "Status not found";

    public static void cleanBase() {
        try (Connection connect = getConnection()) {
            QUERY_RUNNER.execute(connect, "DELETE FROM credit_request_entity");
            QUERY_RUNNER.execute(connect, "DELETE FROM payment_entity");
            QUERY_RUNNER.execute(connect, "DELETE FROM order_entity ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getStatusPaymentEntity() {
        try (Connection connect = getConnection()) {
            String queryStatus = "SELECT status FROM payment_entity";
            return QUERY_RUNNER.query(connect, queryStatus, new ScalarHandler<>());
        } catch (SQLException e) {
            e.printStackTrace();
            return notFound;
        }
    }

    public static String getStatusCreditEntity() {
        try (Connection connect = getConnection()) {
            String queryStatus = "SELECT status FROM credit_request_entity";
            return QUERY_RUNNER.query(connect, queryStatus, new ScalarHandler<>());
        } catch (SQLException e) {
            e.printStackTrace();
            return notFound;
        }
    }
}