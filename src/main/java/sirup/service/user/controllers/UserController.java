package sirup.service.user.controllers;

import com.google.gson.Gson;
import sirup.service.user.api.rpc.SirupAuthClient;
import sirup.service.user.database.IDatabase;
import sirup.service.user.dto.User;
import sirup.service.user.util.PlaceHolderResponse;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserController extends AbstractController {

    private final SirupAuthClient authClient;
    private final Gson gson;

    public UserController(Connection connection) {
        super(connection);
        this.authClient = new SirupAuthClient();
        this.gson = new Gson();
    }

    public Object login(Request request, Response response) {
        System.out.println("LOGIN");
        LoginResponse loginResponse = new LoginResponse(this.authClient.token().orElse("ERROR"));
        response.header("Content-Type","application/json");
        return this.gson.toJson(loginResponse);
    }
    private record LoginResponse(String token){}

    public Object store(Request request, Response response) {
        System.out.println("STORE");

        String insertQuery = "INSERT INTO users (userID, userName, password) VALUES (?, ?, ?);";
        String selectQuery = "SELECT * FROM users WHERE userID = ?";
        StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
        User user = null;
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            UUID id = UUID.randomUUID();
            insertStatement.setString(1, id.toString());
            insertStatement.setString(2, storeRequest.userName());
            insertStatement.setString(3, storeRequest.password());

            insertStatement.execute();

            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id.toString());

            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();

            user = new User(UUID.fromString(resultSet.getString(1)), resultSet.getString(2), resultSet.getString(3));
            this.authClient.token();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        StoreResponse storeResponse = new StoreResponse(user);
        response.header("Content-Type","application/json");
        System.out.println(user);
        return this.gson.toJson(storeResponse);
    }
    private record StoreRequest(String token, String userName, String password) {}
    private record StoreResponse(User user) {}

    public Object update(Request request, Response response) {
        System.out.println("UPDATE");
        response.header("Content-Type","application/json");
        return this.gson.toJson(new PlaceHolderResponse("Not Yet Implemented"));
    }

    public Object remove(Request request, Response response) {
        System.out.println("REMOVE");
        response.header("Content-Type","application/json");
        return this.gson.toJson(new PlaceHolderResponse("Not Yet Implemented"));
    }
}
