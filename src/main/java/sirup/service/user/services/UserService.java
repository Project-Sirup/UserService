package sirup.service.user.services;

import sirup.service.user.dto.User;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static sirup.service.log.rpc.client.ColorUtil.*;

public class UserService extends AbstractService<User> {

    public String add(User user) throws CouldNotMakeResourceException {
        try {
            String insertQuery = "INSERT INTO users (userID, userName, password, systemaccess) VALUES (?, ?, ?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, user.userId());
            insertStatement.setString(2, user.userName());
            insertStatement.setString(3, user.password());
            insertStatement.setInt(4, user.systemAccess().id);
            insertStatement.execute();
            logger.info(name("User"), id(user.getId()), action("created"));
            return user.getId();
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

    public List<User> getAll() throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT * FROM users;";
            Statement selectStatement = this.connection.createStatement();
            ResultSet resultSet = selectStatement.executeQuery(selectQuery);
            List<User> users = new ArrayList<>();
            while(resultSet != null && resultSet.next()) {
                try {
                    User user = User.fromResultSetWithPass(resultSet);
                    users.add(user);
                } catch (CouldNotMakeResourceException ignored) {}
            }
            logger.info(name("Users"), action("found"));
            return users;
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find any users");
        }
    }

    public User get(String id) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT * FROM users WHERE userID = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            logger.info(name("User"), id(id), action("found"));
            return User.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find user with id: " + id);
        } catch (CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public Set<User> search(String userName, int amount) throws ResourceNotFoundException {
        Set<User> users = new TreeSet<>();
        try {
            String selectQuery = "SELECT userName, userId FROM users WHERE starts_with(userName, ?)";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, userName);
            ResultSet resultSet = selectStatement.executeQuery();
            int i = 0;
            while(resultSet.next() && i < amount) {
                try {
                    User user = User.fromResultSetPublic(resultSet);
                    users.add(user);
                    i++;
                } catch (CouldNotMakeResourceException ignored) {}
            }
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
        logger.info(name("Users"),name(userName),action("searched"));
        return users;
    }

    public User getBy(String columnName, String key) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT * FROM users WHERE " + columnName + " = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, key);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            User user = User.fromResultSetWithPass(resultSet);
            logger.info(name("User"), id(user.getId()), action("found"));
            return user;
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find user: " + key);
        } catch (CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public boolean update(User user) throws ResourceNotFoundException {
        try {
            String updateQuery = "UPDATE users SET userName = ?, password = ? WHERE userId = ?";
            PreparedStatement updateStatement = this.connection.prepareStatement(updateQuery);
            updateStatement.setString(1, user.userName());
            updateStatement.setString(2, user.password());
            updateStatement.setString(3, user.userId());
            updateStatement.executeUpdate();
            logger.info(name("User"), id(user.getId()), action("updated"));
            return updateStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public boolean delete(String id) throws ResourceNotFoundException {
        try {
            String deleteQuery = "DELETE FROM users WHERE userID = ?";
            PreparedStatement deleteStatement = this.connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, id);
            deleteStatement.executeUpdate();
            logger.info(name("User"), id(id), action("deleted"));
            return deleteStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException(e.getMessage());
        }
    }
}
