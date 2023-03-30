package sirup.service.user.controllers;

import sirup.service.auth.rpc.client.AuthServiceUnavailableException;
import sirup.service.user.api.Context;
import sirup.service.user.dto.User;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

import static sirup.service.log.rpc.client.ColorUtil.*;

public class UserController extends AbstractController {

    public UserController(final Context context) {
        super(context);
    }

    public Object login(Request request, Response response) {
        try {
            LoginRequest loginRequest = this.gson.fromJson(request.body(), LoginRequest.class);
            User user = this.users.getBy("userName", loginRequest.userName());
            if (!user.password().equals(loginRequest.password())) {
                return this.sendResponseAsJson(response, new ReturnObj<>(Status.UNAUTHORIZED));
            }
            String token = this.authClient.token(user.userId());
            logger.info(id(user.userId()) + " -> " + action("login"));
            return this.sendResponseAsJson(response,
                    new ReturnObj<>("Login success",
                            new LoginResponse(token, user)));
        } catch (CouldNotMakeResourceException | ResourceNotFoundException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.UNAUTHORIZED, e.getMessage()));
        } catch (AuthServiceUnavailableException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.SERVICE_UNAVAILABLE, "AuthenticationService unavailable"));
        }
    }

    public Object find(Request request, Response response) {
        try {
            String userId = request.headers("UserId");
            User user = this.users.get(userId);
            logger.info(id(userId) + " -> " + action("found"));
            return user;
        } catch (ResourceNotFoundException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST));
        }
    }

    private record LoginResponse(String token, User user){}
    private record LoginRequest(String userName, String password) {}

    public Object store(Request request, Response response) {
        try {
            StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
            User user = new User(storeRequest.userName(), storeRequest.password());
            this.users.add(user);
            String token = this.authClient.token(user.userId());
            logger.info(id(user.userId()) + " -> " + action("created"));
            return this.sendResponseAsJson(response, new ReturnObj<>("User created", new StoreResponse(token, user.userId().toString())));
        } catch (CouldNotMakeResourceException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.ALREADY_EXISTS, e.getMessage()));
        } catch (AuthServiceUnavailableException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.SERVICE_UNAVAILABLE, "AuthenticationService is unavailable"));
        }
    }
    private record StoreRequest(String userName, String password) {}
    private record StoreResponse(String token, String userId) {}

    public Object update(Request request, Response response) {
        try {
            String userId = request.headers("UserId");
            UpdateRequest updateRequest = this.gson.fromJson(request.body(), UpdateRequest.class);
            if (!this.users.update(new User(userId, updateRequest.userName(), updateRequest.password()))) {
                return this.sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST));
            }
            logger.info(id(userId) + " -> " + action("updated"));
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK));
        } catch (ResourceNotFoundException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST, e.getMessage()));
        }
    }
    private record UpdateRequest(String userName, String password) {}

    public Object remove(Request request, Response response) {
        try {
            RemoveRequest removeRequest = this.gson.fromJson(request.body(), RemoveRequest.class);
            if (!this.users.delete(removeRequest.userId())) {
                return this.sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST));
            }
            logger.info(id(removeRequest.userId()) + " -> " + action("deleted"));
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK, "User deleted"));
        } catch (ResourceNotFoundException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST, e.getMessage()));
        }
    }
    private record RemoveRequest(String userId) {}
}
