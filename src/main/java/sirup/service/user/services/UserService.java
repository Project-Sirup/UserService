package sirup.service.user.services;

import sirup.service.user.dto.User;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService extends AbstractService<User> {

    @Override
    public boolean add(User user) throws CouldNotMakeResourceException {
        try {
            String insertQuery = "INSERT INTO users (userID, userName, password) VALUES (?, ?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, user.userId().toString());
            insertStatement.setString(2, user.userName());
            insertStatement.setString(3, user.password());
            return insertStatement.execute();
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

    @Override
    public User get(String id) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT * FROM users WHERE userID = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            return User.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find user with id: " + id);
        } catch (CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public User getBy(String columnName, String key) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT * FROM users WHERE " + columnName + " = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, key);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            return User.fromResultSetWithPass(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find user: " + key);
        } catch (CouldNotMakeResourceException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public boolean update(User user) throws ResourceNotFoundException {
        try {
            String updateQuery = "UPDATE users SET userName = ? WHERE userId = ?";
            PreparedStatement updateStatement = this.connection.prepareStatement(updateQuery);
            updateStatement.setString(1, user.userName());
            updateStatement.setString(2, user.userId().toString());
            return updateStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public boolean delete(String id) throws ResourceNotFoundException {
        try {
            String deleteQuery = "DELETE FROM users WHERE userID = ?";
            PreparedStatement deleteStatement = this.connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, id);
            deleteStatement.executeUpdate();
            return deleteStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException(e.getMessage());
        }
    }
}
