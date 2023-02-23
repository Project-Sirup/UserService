package sirup.service.user.api;

import io.grpc.netty.shaded.io.netty.util.internal.logging.Slf4JLoggerFactory;
import org.eclipse.jetty.server.Slf4jRequestLogWriter;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.simple.SimpleLogger;
import sirup.service.user.controllers.OrganisationController;
import sirup.service.user.controllers.ProjectController;
import sirup.service.user.controllers.UserController;
import sirup.service.user.database.IDatabase;
import sirup.service.user.util.SirupLogger;

import java.util.Optional;

import static spark.Spark.*;

public class Api {

    private Api() {
        this.logger = SirupLogger.getInstance();
    }

    private IDatabase database;
    private final SirupLogger logger;

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
        logger.info("Starting service...");
        logger.warn("WARN");
        if (!this.database.connect()) {
            return;
        }
        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "*");
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Allow-Credentials-Header", "*");
            //response.header("Content-Type", "text/event-stream");
        });
        options("/*", ((request, response) -> {
            Optional.ofNullable(request.headers("Access-Control-Request-Headers"))
                    .ifPresent(header -> response.header("Access-Control-Allow-Headers", header));

            Optional.ofNullable(request.headers("Access-Control-Request-Method"))
                    .ifPresent(header -> response.header("Access-Control-Allow-Methods", header));
            return "";
        }));
        path("api/v1", () -> {
            get("",((request, response) -> "Sirup UserService"));
            organisationRoutes();
            projectRoutes();
            userRoutes();
        });

        logger.info("Service Running, Listening @ http://127.0.0.1:" + port() + "/api/v1");
    }

    public void organisationRoutes() {
        final OrganisationController oc = new OrganisationController(this.database.getConnection());
        path("/organisation", () -> {
            post("",   oc::store);
            post("/addUser", oc::addUser);
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
            post("/login",  uc::login);
            put("",         uc::update);
            delete("",      uc::remove);
        });
    }
}
