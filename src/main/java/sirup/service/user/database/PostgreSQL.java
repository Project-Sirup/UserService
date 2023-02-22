package sirup.service.user.database;

import sirup.service.user.dto.User;

import java.sql.*;

public class PostgreSQL implements IDatabase {

    private final String connectionString = "jdbc:postgresql://localhost:5432/sirupuser";
    private final String user = "postgres";
    private final String password = "admin";

    private Connection connection;

    @Override
    public boolean connect() {
        try {
            this.connection = DriverManager.getConnection(connectionString, user, password);
            System.out.println("Connected to PostgreSQL");
            String query = "SELECT * FROM users;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                System.out.println(new User(resultSet.getString("userName"), resultSet.getString("password")));
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
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
