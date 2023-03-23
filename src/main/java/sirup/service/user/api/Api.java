package sirup.service.user.api;

import sirup.service.user.api.middleware.AuthMiddleware;
import sirup.service.user.controllers.OrganisationController;
import sirup.service.user.controllers.ProjectController;
import sirup.service.user.controllers.MicroserviceController;
import sirup.service.user.controllers.UserController;
import sirup.service.user.services.AbstractService;
import sirup.service.user.util.SirupLogger;
import spark.Filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

import static spark.Spark.*;

public class Api {

    private Context context;
    private final SirupLogger logger;
    private final Filter authMiddleWare;
    private final String doc;

    private Api() {
        this.logger = SirupLogger.getInstance();
        this.authMiddleWare = new AuthMiddleware();
        doc = getDocFromFile();
    }

    private String getDocFromFile() {
        try {
            URL url = this.getClass().getClassLoader().getResource("json/doc.json");
            File docFile = new File(url.toURI());
            try (Scanner input = new Scanner(docFile)) {
                StringBuilder stringBuilder = new StringBuilder();
                while (input.hasNextLine()) {
                    stringBuilder.append(input.nextLine());
                }
                return stringBuilder.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    private void setPort(final int port) {
        port(port);
    }

    private void setContext(final Context context) {
        this.context = context;
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

        public ApiBuilder context(final Context.ContextBuilder contextBuilder) {
            this.context(contextBuilder.build());
            return this;
        }
        public ApiBuilder context(final Context context) {
            this.api.setContext(context);
            return this;
        }

        public Api build() {
            return this.api;
        }

    }

    public void start() {
        logger.info("Starting service...");
        if (!this.context.getDatabase().connect()) {
            logger.info("Could not connect to database");
            return;
        }
        this.context.getServices().forEach(AbstractService::init);
        port(2103);
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
            defaultRoutes();
            organisationRoutes();
            projectRoutes();
            serviceRoutes();
            userRoutes();
        });

        logger.info("Service Running, Listening @ http://127.0.0.1:" + port() + "/api/v1");
    }
    private void defaultRoutes() {
        get("/health", ((request, response) -> {
            return "ok";
        }));
        get("", ((request, response) -> {
            response.header("Content-Type","application/json");
            return doc;
        }));
    }

    public void organisationRoutes() {
        final OrganisationController oc = new OrganisationController(this.context);
        path("/organisation", () -> {
            before("",                          this.authMiddleWare);
            get("/:organisationId",             oc::find);
            post("",                            oc::store);
            //post("/:organisationId/user/invite",oc::inviteUser);
            post("/:organisationId/user/accept",oc::addUser);
            put("",                             oc::update);
            delete("",                          oc::remove);
        });
    }

    public void projectRoutes() {
        final ProjectController pc = new ProjectController(this.context);
        path("/project", () -> {
            before("", this.authMiddleWare);
            post("",    pc::store);
            put("",     pc::update);
            delete("",  pc::remove);
        });
    }

    public void serviceRoutes() {
        final MicroserviceController sc = new MicroserviceController(this.context);
        path("/service", () -> {
            before("", this.authMiddleWare);
            post("", sc::store);
        });
    }

    public void userRoutes() {
        final UserController uc = new UserController(this.context);
        path("/user", () -> {
            before("/id/:userId",  this.authMiddleWare);
            post("/login",      uc::login);
            post("",            uc::store);
            put("/id/:userId",     uc::update);
            delete("/id/:userId",  uc::remove);

        });
    }
}
