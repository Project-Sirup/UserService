package sirup.service.user.controllers;

import com.google.gson.Gson;
import sirup.service.auth.rpc.client.AuthClient;
import sirup.service.log.rpc.client.LogClient;
import sirup.service.user.api.Context;
import sirup.service.user.dto.Microservice;
import sirup.service.user.dto.Organisation;
import sirup.service.user.dto.Project;
import sirup.service.user.dto.User;
import sirup.service.user.services.*;
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
    }

    protected String sendResponseAsJson(Response response, ReturnObj returnObj) {
        response.header("Content-Type","application/json");
        response.status(returnObj.statusCode());
        return this.gson.toJson(returnObj);
    }

    protected static record ReturnObj<T>(int statusCode, String message, T data) {
        public ReturnObj(Status status) {
            this(status.getCode(), status.getMessage(), null);
        }
        public ReturnObj(Status status, String message) {
            this(status.getCode(), message, null);
        }
        public ReturnObj(String message, T t) {
            this(Status.OK.getCode(), message, t);
        }
    }
}
