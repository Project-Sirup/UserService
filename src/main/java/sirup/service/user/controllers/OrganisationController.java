package sirup.service.user.controllers;

import sirup.service.user.api.Context;
import sirup.service.user.dto.Organisation;
import sirup.service.user.dto.OrganisationPermission;
import sirup.service.user.dto.PermissionLevel;
import sirup.service.user.dto.User;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;
import sirup.service.user.util.ReturnObj;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

import java.util.List;

public class OrganisationController extends AbstractController {

    public OrganisationController(final Context context) {
        super(context);
    }

    public Object find(Request request, Response response) {
        try {
            String organisationId = request.params("organisationId");
            Organisation organisation = this.organisations.get(organisationId);
            return this.sendResponseAsJson(response, new ReturnObj<>("Organisation found", organisation));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }

    public Object store(Request request, Response response) {
        try {
            StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
            String userId = request.headers("UserId");
            Organisation organisation = new Organisation(storeRequest.organisationName());
            this.organisations.add(organisation);
            OrganisationPermission organisationPermission = new OrganisationPermission(
                    organisation.organisationId(),
                    userId,
                    PermissionLevel.OWNER);
            this.organisationPermissions.add(organisationPermission);
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.CREATED, "Organisation created", organisation));
        } catch (CouldNotMakeResourceException e) {
            return this.returnBadRequest(response, e.getMessage());
        }
    }
    private record StoreRequest(String organisationName) {}

    public Object findUsers(Request request, Response response) {
        try {
            String userId = request.params("organisationId");
            List<User> users = this.organisations.getUsers(userId);
            return this.sendResponseAsJson(response, new ReturnObj<>("Users found", users));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }


    public Object findAll(Request request, Response response) {
        try {
            String userId = request.headers("UserId");
            List<Organisation> organisations = this.organisations.getAll(userId);
            return this.sendResponseAsJson(response, new ReturnObj<>("Organisations found", organisations));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }

    public Object update(Request request, Response response) {
        try {
            String organisationId = request.params("organisationId");
            String userId = request.headers("UserId");
            UpdateRequest updateRequest = this.gson.fromJson(request.body(), UpdateRequest.class);
            Organisation organisation = new Organisation(organisationId, updateRequest.organisationName());
            if (this.organisations.update(organisation, userId)) {
                return this.sendResponseAsJson(response, new ReturnObj<>("Organisation updated", organisation));
            }
            return this.returnDoesNotExist(response);
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }
    private record UpdateRequest(String organisationName) {};

    public Object remove(Request request, Response response) {
        try {
            String organisationId = request.params("organisationId");
            if (this.organisations.delete(organisationId)) {
                return this.sendResponseAsJson(response, new ReturnObj<>( "Organisation deleted"));
            }
            return this.returnDoesNotExist(response);
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }
}
