package sirup.service.user;

import sirup.service.auth.rpc.client.AuthClient;
import sirup.service.log.rpc.client.LogClient;
import sirup.service.user.api.Api;
import sirup.service.user.api.Context;
import sirup.service.user.database.PostgreSQL;
import sirup.service.user.dto.Microservice;
import sirup.service.user.dto.Organisation;
import sirup.service.user.dto.Project;
import sirup.service.user.dto.User;
import sirup.service.user.services.MicroserviceService;
import sirup.service.user.services.OrganisationService;
import sirup.service.user.services.ProjectService;
import sirup.service.user.services.UserService;
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
                                .addService(Microservice.class, new MicroserviceService()))
                .build()
                .start();
    }
}
