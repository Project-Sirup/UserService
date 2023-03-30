package sirup.service.user;

import sirup.service.auth.rpc.client.AuthClient;
import sirup.service.log.rpc.client.LogClient;
import sirup.service.user.api.Api;
import sirup.service.user.api.Context;
import sirup.service.user.database.PostgreSQL;
import sirup.service.user.dto.*;
import sirup.service.user.services.*;
import sirup.service.user.util.Env;

public class Main {
    public static void main(String[] args) {
        AuthClient.init(Env.AUTH_URL, Env.AUTH_PORT);
        LogClient.init(Env.LOG_URL, Env.LOG_PORT, "UserService");
        Api.builder()
                .port(Env.API_PORT)
                .context(
                        Context.builder()
                                .database(new PostgreSQL())
                                .addService(User.class, new UserService())
                                .addService(Organisation.class, new OrganisationService())
                                .addService(Project.class, new ProjectService())
                                .addService(Microservice.class, new MicroserviceService())
                                .addService(OrganisationPermission.class, new OrganisationPermissionService())
                                .addService(Invite.class, new InviteService())
                                .addService(ProjectPermission.class, new ProjectPermissionService()))
                .build()
                .start();
    }
}
