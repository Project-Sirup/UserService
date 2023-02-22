package sirup.service.user.api;

import sirup.service.user.controllers.OrganisationController;
import sirup.service.user.controllers.ProjectController;
import sirup.service.user.controllers.UserController;
import sirup.service.user.database.IDatabase;
import spark.Filter;

import java.util.Optional;

import static spark.Spark.*;

public class Api {

    private Api() {

    }

    private IDatabase database;

    private void setPort(final int port) {
        port(port);
    }

    private void setDatabase(final IDatabase database) {
        this.database = database;
    }

    public static ApiBuilder builder() {
        return new ApiBuilder(new Api());
    }

    public static class ApiBuilder {
        private final Api api;

        private ApiBuilder(final Api api) {
            this.api = api;
        }

        public ApiBuilder port(final int port) {
            this.api.setPort(port);
            return this;
        }

        public ApiBuilder database(final IDatabase database) {
            this.api.setDatabase(database);
            return this;
        }

        public Api build() {
            return this.api;
        }

    }

    public void start() {

        if (!this.database.connect()) {
            return;
        }

        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "*");
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Allow-Credentials-Header", "*");
            response.header("Content-Type", "text/event-stream");
        });
        options("/*", ((request, response) -> {
            Optional.ofNullable(request.headers("Access-Control-Request-Headers"))
                    .ifPresent(header -> response.header("Access-Control-Allow-Headers", header));

            Optional.ofNullable(request.headers("Access-Control-Request-Method"))
                    .ifPresent(header -> response.header("Access-Control-Allow-Methods", header));
            return "";
        }));
        path("api/v1", () -> {
            organisationRoutes();
            projectRoutes();
            userRoutes();
        });
    }

    public void organisationRoutes() {
        final OrganisationController oc = new OrganisationController(this.database.getConnection());
        path("/organisation", () -> {
            post("",   oc::store);
            put("",    oc::update);
            delete("", oc::remove);
        });
    }

    public void projectRoutes() {
        final ProjectController pc = new ProjectController(this.database.getConnection());
        path("/project", () -> {
            post("",    pc::store);
            put("",     pc::update);
            delete("",  pc::remove);
        });
    }

    public void userRoutes() {
        final UserController uc = new UserController(this.database.getConnection());
        path("/user", () -> {
            post("",        uc::store);
            get("/login",   uc::login);
            put("",         uc::update);
            delete("",      uc::remove);
        });
    }
}
