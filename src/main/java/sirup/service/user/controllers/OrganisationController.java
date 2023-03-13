package sirup.service.user.controllers;

import sirup.service.user.api.Context;
import sirup.service.user.dto.Organisation;
import sirup.service.user.dto.PrivilegeLevel;
import sirup.service.user.dto.User;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

public class OrganisationController extends AbstractController {

    public OrganisationController(final Context context) {
        super(context);
    }

    public Object find(Request request, Response response) {
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.NOT_IMPLEMENTED));
    }

    public Object store(Request request, Response response) {
        StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
        this.organisations.add(storeRequest.organisation());
        this.organisations.userPermission.add(storeRequest.organisation(), storeRequest.user(), PrivilegeLevel.OWNER);
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.CREATED));
    }
    private record StoreRequest(User user, String token, Organisation organisation) {}

    public Object addUser(Request request, Response response) {
        InviteRequest inviteRequest = this.gson.fromJson(request.body(), InviteRequest.class);
        this.organisations.userInvite.add(
                inviteRequest.sender(),
                inviteRequest.receiver(),
                inviteRequest.organisation());
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.NOT_IMPLEMENTED));
    }
    private record InviteRequest(User sender, User receiver, Organisation organisation) {}

    public Object update(Request request, Response response) {
        System.out.println("UPDATE");
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.NOT_IMPLEMENTED));
    }

    public Object remove(Request request, Response response) {
        System.out.println("REMOVE");
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.NOT_IMPLEMENTED));
    }
}
