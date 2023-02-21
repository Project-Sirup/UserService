package sirup.service.user.controllers;

import com.google.gson.Gson;
import sirup.service.user.api.rpc.SirupAuthClient;
import sirup.service.user.database.IDatabase;
import sirup.service.user.util.PlaceHolderResponse;
import spark.Request;
import spark.Response;

public class UserController extends AbstractController {

    private final SirupAuthClient authClient;
    private final Gson gson;

    public UserController(IDatabase database) {
        super(database);
        this.authClient = new SirupAuthClient();
        this.gson = new Gson();
    }

    public Object login(Request request, Response response) {
        System.out.println("LOGIN");
        LoginRequest loginRequest = this.gson.fromJson(request.body(), LoginRequest.class);
        LoginResponse loginResponse = new LoginResponse(this.authClient.token(loginRequest.username(), loginRequest.password()).orElse("ERROR"));
        response.header("Content-Type","application/json");
        return this.gson.toJson(loginResponse);
    }
    private record LoginResponse(String token){}
    private record  LoginRequest(String username, String password) {}

    public Object store(Request request, Response response) {
        System.out.println("STORE");
        StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
        StoreResponse storeResponse = new StoreResponse(this.authClient.auth(storeRequest.token()));
        response.header("Content-Type","application/json");
        return this.gson.toJson(storeResponse);
    }
    private record StoreResponse(boolean tokenValid) {}
    private record StoreRequest(String token) {}

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
