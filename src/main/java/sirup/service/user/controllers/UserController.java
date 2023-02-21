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
        LoginResponse loginResponse = new LoginResponse(this.authClient.token().orElse("ERROR"));
        response.header("Content-Type","application/json");
        return this.gson.toJson(loginResponse);
    }
    private record LoginResponse(String token){}

    public Object store(Request request, Response response) {
        System.out.println("STORE");
        StoreResponse storeResponse = new StoreResponse(this.authClient.auth());
        response.header("Content-Type","application/json");
        return this.gson.toJson(storeResponse);
    }
    private record StoreResponse(boolean tokenValid) {}

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
