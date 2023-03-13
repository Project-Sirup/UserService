package sirup.service.user.database;

import sirup.service.user.dto.User;
import sirup.service.user.util.SirupLogger;

import java.sql.*;
import java.util.UUID;

public class PostgreSQL implements IDatabase {

    private final String connectionString = "jdbc:postgresql://localhost:5432/sirupuser";
    private final String user = "postgres";
    private final String password = "admin";

    private Connection connection;

    @Override
    public boolean connect() {
        try {
            this.connection = DriverManager.getConnection(connectionString, user, password);
            SirupLogger.getInstance().info("Connected to PostgreSQL");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
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
