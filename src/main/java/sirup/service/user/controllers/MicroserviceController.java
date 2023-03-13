package sirup.service.user.controllers;

import sirup.service.user.api.Context;
import sirup.service.user.dto.Microservice;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

import java.util.UUID;


public class MicroserviceController extends AbstractController {

    public MicroserviceController(final Context context) {
        super(context);
    }

    public Object store(Request request, Response response) {
        StoreRequest storeRequest = gson.fromJson(request.body(), StoreRequest.class);
        Microservice microservice = new Microservice(storeRequest.microserviceName(), UUID.fromString(storeRequest.projectID()));
        microservices.add(microservice);



        return this.sendResponseAsJson(response, new ReturnObj<>(Status.CREATED));
    }
    private record StoreRequest(String microserviceName, String projectID, String userID) {}
}
