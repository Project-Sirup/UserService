package sirup.service.user.controllers;

import com.google.gson.Gson;
import sirup.service.auth.rpc.client.AuthClient;
import sirup.service.log.rpc.client.LogClient;
import sirup.service.user.api.Context;
import sirup.service.user.dto.*;
import sirup.service.user.services.*;
import sirup.service.user.util.ReturnObj;
import sirup.service.user.util.Status;
import spark.Response;

import java.sql.Connection;

public abstract class AbstractController {
    protected final Context context;
    protected final Connection connection;
    protected final Gson gson;
    protected final LogClient logger;
    protected final AuthClient authClient;

    //-----Services-----
    protected final UserService users;
    protected final OrganisationService organisations;
    protected final ProjectService projects;
    protected final MicroserviceService microservices;
    protected final InviteService invites;
    protected final OrganisationPermissionService organisationPermissions;
    protected final ProjectPermissionService projectPermissions;
    protected final MicroservicePermissionService microservicePermissions;

    public AbstractController(final Context context) {
        this.context = context;
        this.connection = this.context.getDatabase().getConnection();
        this.logger = LogClient.getInstance();
        this.gson = new Gson();
        this.authClient = AuthClient.getInstance();

        this.users = (UserService) this.context.getService(User.class);
        this.organisations = (OrganisationService) this.context.getService(Organisation.class);
        this.projects = (ProjectService) this.context.getService(Project.class);
        this.microservices = (MicroserviceService) this.context.getService(Microservice.class);
        this.invites = (InviteService) this.context.getService(Invite.class);
        this.organisationPermissions = (OrganisationPermissionService) this.context.getService(OrganisationPermission.class);
        this.projectPermissions = (ProjectPermissionService) this.context.getService(ProjectPermission.class);
        this.microservicePermissions = (MicroservicePermissionService) this.context.getService(MicroservicePermission.class);
    }

    protected String sendResponseAsJson(Response response, ReturnObj<?> returnObj) {
        response.header("Content-Type","application/json");
        response.status(returnObj.statusCode());
        return this.gson.toJson(returnObj);
    }

    protected String returnDoesNotExist(Response response, String message) {
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST, message));
    }
    protected String returnDoesNotExist(Response response) {
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST));
    }

    protected String returnBadRequest(Response response) {
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.BAD_REQUEST));
    }
    protected String returnBadRequest(Response response, String message) {
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.BAD_REQUEST, message));
    }
}
