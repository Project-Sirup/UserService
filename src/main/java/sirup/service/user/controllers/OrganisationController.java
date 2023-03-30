package sirup.service.user.controllers;

import sirup.service.user.api.Context;
import sirup.service.user.dto.Organisation;
import sirup.service.user.dto.OrganisationPermission;
import sirup.service.user.dto.PrivilegeLevel;
import sirup.service.user.dto.User;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

import java.util.List;

public class OrganisationController extends AbstractController {

    public OrganisationController(final Context context) {
        super(context);
    }

    public Object find(Request request, Response response) {
        String organisationId = request.params("organisationId");
        Organisation organisation = this.organisations.get(organisationId);
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK, organisation));
    }

    public Object store(Request request, Response response) {
        StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
        Organisation organisation = new Organisation(storeRequest.organisationName());
        this.organisations.add(organisation);
        OrganisationPermission organisationPermission = new OrganisationPermission(organisation.organisationId(), storeRequest.userId(), PrivilegeLevel.OWNER);
        this.organisationPermissions.add(organisationPermission);
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.CREATED, new StoreResponse(organisation.getId())));
    }
    private record StoreRequest(String userId, String organisationName) {}
    private record StoreResponse(String organisationId) {};

    public Object findAll(Request request, Response response) {
        String userId = request.headers("UserId");
        List<Organisation> organisations = this.organisations.getAll(userId);
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK, new FindAllResponse(organisations)));
    }
    private record FindAllResponse(List<Organisation> organisations) {}


    public Object update(Request request, Response response) {
        String organisationId = request.params("organisationId");
        UpdateRequest updateRequest = this.gson.fromJson(request.body(), UpdateRequest.class);
        Organisation organisation = new Organisation(organisationId, updateRequest.organisationName());
        if (this.organisations.update(organisation)) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK));
        }
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST));
    }
    private record UpdateRequest(String organisationName) {};

    public Object remove(Request request, Response response) {
        String organisationId = request.params("organisationId");
        if (this.organisations.delete(organisationId)) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK));
        }
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST));
    }
}
