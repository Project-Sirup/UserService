package sirup.service.user.controllers;

import sirup.service.user.api.Context;
import sirup.service.user.dto.Microservice;
import sirup.service.user.dto.MicroservicePermission;
import sirup.service.user.dto.PermissionLevel;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;
import sirup.service.user.util.ReturnObj;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

import java.util.List;

public class MicroserviceController extends AbstractController {

    public MicroserviceController(final Context context) {
        super(context);
    }

    public Object store(Request request, Response response) {
        try {
            StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
            String userId = request.headers("UserID");
            Microservice microservice = new Microservice(storeRequest.microserviceName(), storeRequest.projectId());
            this.microservices.add(microservice);
            MicroservicePermission microservicePermission = new MicroservicePermission(
                    microservice.microserviceId(),
                    userId,
                    PermissionLevel.OWNER);
            this.microservicePermissions.add(microservicePermission);
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.CREATED, "Microservice created", microservice));
        } catch (CouldNotMakeResourceException e) {
            return this.returnBadRequest(response, e.getMessage());
        }
    }
    private record StoreRequest(String microserviceName, String projectId) {}

    public Object find(Request request, Response response) {
        try {
            String microserviceId = request.params("microserviceId");
            Microservice microservice = this.microservices.get(microserviceId);
            return this.sendResponseAsJson(response, new ReturnObj<>("Microservice found", microservice));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }

    public Object findAll(Request request, Response response) {
        try {
            String projectId = request.params("projectId");
            List<Microservice> microservices = this.microservices.getAll(projectId);
            return this.sendResponseAsJson(response, new ReturnObj<>("Microservices found", microservices));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }

    public Object update(Request request, Response response) {
        try {
            UpdateRequest updateRequest = this.gson.fromJson(request.body(), UpdateRequest.class);
            String microserviceId = request.params("microserviceId");
            Microservice microservice = new Microservice(microserviceId,
                    updateRequest.microserviceName(),
                    updateRequest.projectId(),
                    updateRequest.microserviceFile());
            if (!this.microservices.update(microservice)) {
                return this.returnDoesNotExist(response);
            }
            return this.sendResponseAsJson(response, new ReturnObj<>("Microservice updated", microservice));
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }
    private record UpdateRequest(String microserviceName, String projectId, Object microserviceFile) {}

    public Object save(Request request, Response response) {

        return this.returnBadRequest(response);
    }
    private record SaveRequest(Object microserviceFile) {}

    public Object delete(Request request, Response response) {
        try {
            String microserviceId = request.params("microserviceId");
            if (!this.microservices.delete(microserviceId)) {
                return this.returnDoesNotExist(response);
            }
            return this.sendResponseAsJson(response, new ReturnObj<>("Microservice deleted"));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }

}
