package sirup.service.user.controllers;

import sirup.service.user.dto.User;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserController extends AbstractController {

    public UserController(Connection connection) {
        super(connection);
    }

    public Object login(Request request, Response response) {
        LoginRequest loginRequest = this.gson.fromJson(request.body(), LoginRequest.class);
        Optional<User> optionalUser = getUserFromUserName(loginRequest.userName());
        if (optionalUser.isEmpty()) {
            return this.sendResponseAsJson(response ,new ReturnObj<>(Status.UNAUTHORIZED));
        }
        User user = optionalUser.get();
        if (!user.password().equals(loginRequest.password())) {
            return this.sendResponseAsJson(response ,new ReturnObj<>(Status.UNAUTHORIZED));
        }
        logger.info("/// Login user with id = " + user.userID());
        return this.sendResponseAsJson(response, new ReturnObj<>("Login success",
                new LoginResponse(this.authClient.token(user.userID().toString()).getOr("ERROR"), user)));
    }
    private record LoginResponse(String token, User user){}
    private record LoginRequest(String userName, String password) {}

    public Object store(Request request, Response response) {
        StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
        String token = "TOKEN";
        User u = new User(storeRequest.userName(), storeRequest.password());
        UUID id = u.userID();
        if (!insertUser(u)) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.ALREADY_EXISTS));
        }
        Optional<User> optionalUser = this.getUser(id);
        if (optionalUser.isEmpty()) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.ALREADY_EXISTS));
        }
        User user = optionalUser.get();
        token = this.authClient.token(user.userID().toString()).getOr("ERROR");
        logger.info("+++ Created user with id = " + user.userID());
        return this.sendResponseAsJson(response, new ReturnObj<>("User created", new StoreResponse(token, user)));
    }
    private record StoreRequest(String userName, String password) {}
    private record StoreResponse(String token,User user) {}

    public Object update(Request request, Response response) {
        logger.info("### Update user");
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK, "Not Yet Implemented"));
    }

    public Object remove(Request request, Response response) {
        RemoveRequest removeRequest = this.gson.fromJson(request.body(), RemoveRequest.class);
        if (!this.deleteUser(removeRequest.userID())) {
            return sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST));
        }
        logger.info("--- Deleted user with id = " + removeRequest.userID());
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK, "User deleted"));
    }
    private record RemoveRequest(String userID) {}
    private record RemoveResponse(String message) {}

    public boolean deleteUser(UUID id) {
        return this.deleteUser(id.toString());
    }

    public boolean deleteUser(String id) {
        try {
            String deleteQuery = "DELETE FROM users WHERE userID = ?";
            PreparedStatement deleteStatement = this.connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, id);
            deleteStatement.executeUpdate();
            return deleteStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean insertUser(User user) {
        try {
            String insertQuery = "INSERT INTO users (userID, userName, password) VALUES (?, ?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            for (Field field : user.getClass().getFields()) {
                logger.info(field.getName());
            }
            insertStatement.setString(1, user.userID().toString());
            insertStatement.setString(2, user.userName());
            insertStatement.setString(3, user.password());
            insertStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Optional<User> getUserFromUserName(String userName) {
        try {
            String selectQuery = "SELECT * FROM users WHERE userName = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, userName);
            ResultSet resultSet = selectStatement.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();
            }
            return Optional.of(getUserFromResultSet(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> getUser(UUID id) {
        return this.getUser(id.toString());
    }

    public Optional<User> getUser(String id) {
        try {
            String selectQuery = "SELECT * FROM users WHERE userID = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            return Optional.of(getUserFromResultSet(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString("userID"));
        String userName = resultSet.getString("userName");
        String password = resultSet.getString("password");
        return new User(id, userName, password);
    }
}
