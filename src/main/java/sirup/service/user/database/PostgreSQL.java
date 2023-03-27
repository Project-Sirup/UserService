package sirup.service.user.database;

import sirup.service.log.rpc.client.LogClient;
import sirup.service.user.util.Env;

import java.sql.*;

public class PostgreSQL implements IDatabase {

    private final String connectionString = Env.POSTGRES_CONNECTION_STRING;
    private final String user = Env.POSTGRES_USER;
    private final String password = Env.POSTGRES_PASSWORD;
    private final LogClient logger = LogClient.getInstance();

    private Connection connection;

    @Override
    public boolean connect() {
        try {
            this.connection = DriverManager.getConnection(connectionString, user, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            logger.error("Could not connect yo PostgreSQL");
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
