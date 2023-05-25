package sirup.service.user.database;

import sirup.service.log.rpc.client.LogClient;
import sirup.service.user.util.Env;

import java.sql.*;

public class PostgreSQL implements IDatabase {

    private final String connectionString = Env.USER_DB_ADDRESS + ":" + Env.USER_DB_PORT + "/" + Env.USER_DB_NAME;
    private final String user = Env.USER_DB_USERNAME;
    private final String password = Env.USER_DB_PASSWORD;
    private final LogClient logger = LogClient.getInstance();

    private Connection connection;

    @Override
    public boolean connect() {
        try {
            this.connection = DriverManager.getConnection(connectionString, user, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            logger.error("Could not connect to PostgreSQL");
            return false;
        }
        return true;
    }

    @Override
    public boolean disconnect() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
