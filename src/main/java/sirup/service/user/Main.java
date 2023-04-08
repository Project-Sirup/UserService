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
                                .addService(Invite.class, new InviteService())
                                .addService(Organisation.class, new OrganisationService())
                                .addService(OrganisationPermission.class, new OrganisationPermissionService())
                                .addService(Project.class, new ProjectService())
                                .addService(ProjectPermission.class, new ProjectPermissionService())
                                .addService(Microservice.class, new MicroserviceService())
                                .addService(MicroservicePermission.class, new MicroservicePermissionService())
                )
                .build()
                .start();
    }
}
