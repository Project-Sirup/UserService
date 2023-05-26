package sirup.service.user.controllers;

import org.mindrot.jbcrypt.BCrypt;
import sirup.service.auth.rpc.client.AuthServiceUnavailableException;
import sirup.service.user.api.Context;
import sirup.service.user.dto.SystemAccess;
import sirup.service.user.dto.User;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;
import sirup.service.user.util.ReturnObj;
import sirup.service.user.util.Status;
import sirup.service.user.util.Env;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Set;

public class UserController extends AbstractController {

    public UserController(final Context context) {
        super(context);
        addDefaultAdminIfNoExists();
    }

    private void addDefaultAdminIfNoExists() {
        try {
            if (this.users.getAll().isEmpty()) {
                this.users.add(new User(
                        Env.DEFAULT_ADMIN_USERNAME,
                        hashPassword(Env.DEFAULT_ADMIN_PASSWORD),
                        SystemAccess.ADMIN.id));
            }
        } catch (ResourceNotFoundException | CouldNotMakeResourceException e) {
            e.printStackTrace();
        }
    }

    public Object login(Request request, Response response) {
        try {
            LoginRequest loginRequest = this.gson.fromJson(request.body(), LoginRequest.class);
            User user = this.users.getBy("userName", loginRequest.userName());
            System.out.println(user.password());
            System.out.println(loginRequest.password());
            if (!BCrypt.checkpw(loginRequest.password(), user.password())) {
                return this.sendResponseAsJson(response, new ReturnObj<>(Status.UNAUTHORIZED));
            }
            String token = this.authClient.token(user.userId(), user.systemAccess().id);
            return this.sendResponseAsJson(response,
                    new ReturnObj<>("Login success",
                            new LoginResponse(token, user)));
        } catch (ResourceNotFoundException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.UNAUTHORIZED, e.getMessage()));
        } catch (AuthServiceUnavailableException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.SERVICE_UNAVAILABLE, "AuthenticationService unavailable"));
        }
    }
    private record LoginResponse(String token, User user){}
    private record LoginRequest(String userName, String password) {}

    public Object find(Request request, Response response) {
        try {
            String userId = request.headers("UserId");
            User user = this.users.get(userId);
            return this.sendResponseAsJson(response, new ReturnObj<>("User found", user));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response);
        }
    }

    public Object search(Request request, Response response) {
        try {
            String searchString = request.queryParams("userName");
            int searchAmount = Integer.parseInt(request.queryParams("amount"));
            Set<User> users = this.users.search(searchString, searchAmount);
            return this.sendResponseAsJson(response, new ReturnObj<>("User(s) found", users));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }

    private String hashPassword(String plainPass) {
        String salt = BCrypt.gensalt(10);
        String hashedPassword = BCrypt.hashpw(plainPass, salt);
        return hashedPassword;
    }

    public Object store(Request request, Response response) {
        try {
            StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
            System.out.println(storeRequest);
            User user;
            String reqToken = request.headers("Token");
            String reqId = request.headers("UserId");
            String hashPassword = hashPassword(storeRequest.password());
            //For adding new admin users
            if (reqToken != null && reqId != null && authClient.auth(reqToken, reqId,SystemAccess.ADMIN.id)) {
                user = new User(storeRequest.userName(), storeRequest.password(), storeRequest.systemAccess());
                user = new User(storeRequest.userName(), hashPassword, storeRequest.systemAccess());
            }
            else {
                user = new User(storeRequest.userName(), storeRequest.password());
                user = new User(storeRequest.userName(), hashPassword);
            }
            this.users.add(user);
            String token = this.authClient.token(user.userId(), user.systemAccess().id);
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.CREATED, "User created", new StoreResponse(token, user)));
        } catch (CouldNotMakeResourceException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.ALREADY_EXISTS, e.getMessage()));
        } catch (AuthServiceUnavailableException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.SERVICE_UNAVAILABLE, "AuthenticationService is unavailable"));
        }
    }
    private record StoreRequest(String userName, String password, int systemAccess) {}
    private record StoreResponse(String token, User user) {}

    public Object update(Request request, Response response) {
        try {
            String userId = request.headers("UserId");
            UpdateRequest updateRequest = this.gson.fromJson(request.body(), UpdateRequest.class);
            String hashPassword = hashPassword(updateRequest.user().password());
            User user = new User(userId, updateRequest.user().userName(), hashPassword);
            if (!this.users.update(user)) {
                return this.returnDoesNotExist(response);
            }
            return this.sendResponseAsJson(response, new ReturnObj<>( "User updated", user));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response);
        }
    }
    private record UpdateRequest(User user) {}

    public Object remove(Request request, Response response) {
        try {
            String hUserId = request.headers("UserId");
            String pUserId = request.params("userId");
            if (!hUserId.equals(pUserId)) {
                return this.sendResponseAsJson(response, new ReturnObj<>(Status.UNAUTHORIZED));
            }
            if (!this.users.delete(pUserId)) {
                return this.returnDoesNotExist(response);
            }
            return this.sendResponseAsJson(response, new ReturnObj<>("User deleted"));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response);
        }
    }
}
