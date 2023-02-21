package sirup.service.user.controllers;

import com.google.gson.Gson;
import sirup.service.user.database.IDatabase;
import sirup.service.user.util.PlaceHolderResponse;
import spark.Request;
import spark.Response;

public class OrganisationController extends AbstractController {

    private final Gson gson;

    public OrganisationController(IDatabase database) {
        super(database);
        this.gson = new Gson();
    }

    public Object store(Request request, Response response) {
        System.out.println("STORE");
        response.header("Content-Type","application/json");
        return this.gson.toJson(new PlaceHolderResponse("Not Yet Implemented"));
    }

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
