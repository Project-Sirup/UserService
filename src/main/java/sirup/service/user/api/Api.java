package sirup.service.user.api;

import sirup.service.log.rpc.client.LogClient;
import sirup.service.log.rpc.client.LogServiceUnavailableException;
import sirup.service.user.api.middleware.AuthMiddleware;
import sirup.service.user.controllers.*;
import sirup.service.user.services.AbstractService;
import sirup.service.user.util.Env;
import spark.Filter;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

import static spark.Spark.*;

public class Api {

    private Context context;
    private final LogClient logger;
    private final Filter authMiddleWare;
    private final String doc;

    private final String baseUrl = "/api/v1";

    private Api() {
        this.logger = LogClient.getInstance();
        this.authMiddleWare = new AuthMiddleware();
        doc = getDocFromFile();
    }

    private String getDocFromFile() {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("json/doc.json")) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            inputStreamReader.close();
            bufferedReader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
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
        if (!this.context.getDatabase().connect()) {
            logger.error("Failed to start");
            return;
        }
        this.context.getServices().forEach(AbstractService::init);
        before("*",(request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "*");
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Allow-Credentials-Header", "*");
            response.header("Accept", "*/*");
            //response.header("Content-Type", "text/event-stream");
        });
        options("*", ((request, response) -> {
            Optional.ofNullable(request.headers("Access-Control-Request-Headers"))
                    .ifPresent(header -> response.header("Access-Control-Allow-Headers", header));
            Optional.ofNullable(request.headers("Access-Control-Request-Method"))
                    .ifPresent(header -> response.header("Access-Control-Allow-Methods", header));
            Optional.ofNullable(request.headers("Accept"))
                    .ifPresent(header -> response.header("Accept", header));
            response.status(200);
            return "OK";
        }));
        final UserController uc = new UserController(this.context);
        final OrganisationController oc = new OrganisationController(this.context);
        final ProjectController pc = new ProjectController(this.context);
        final MicroserviceController mc = new MicroserviceController(this.context);
        final InviteController ic = new InviteController(this.context);

        path(baseUrl, () -> {
            get("/health", ((request, response) -> "ok"));
            get("", ((request, response) -> {
                response.header("Content-Type","application/json");
                return doc;
            }));
            path("/user", () -> {
                post("/login",                              uc::login);
                post("",                                    uc::store);
            });
            path("/protected", () -> {                      //Protected
                before("/*",                                this.authMiddleWare); //Calls AuthService
                path("/user", () -> {
                    get("/",                                uc::search);
                    put("",                                 uc::update);
                    delete("",                              uc::remove);
                });
                path("/organisation", () -> {
                    get("",                                 oc::findAll);
                    get("/:organisationId",                 oc::find);
                    get("/:organisationId/users",           oc::findUsers);
                    post("",                                oc::store);
                    put("/:organisationId",                 oc::update);
                    delete("/:organisationId",              oc::remove);
                });
                path("/project", () -> {
                    get("/organisation/:organisationId",    pc::findAll);
                    get("/:projectId",                      pc::find);
                    post("",                                pc::store);
                    put("/:projectId",                      pc::update);
                    delete("/:projectId",                   pc::remove);
                });
                path("/microservice", () -> {
                    get("/project/:projectId",              mc::findAll);
                    get("/:microserviceId",                 mc::find);
                    post("",                                mc::store);
                    put("/:microserviceId",                 mc::update);
                    delete("/:microserviceId",              mc::delete);
                });
                path("/invite", () -> {
                    get("/sent",                            ic::findSent);
                    get("/received",                        ic::findReceived);
                    post("",                                ic::store);
                    post("/response",                       ic::response);
                    delete("",                              ic::remove);
                });
            });
        });

        logger.info("Service Running, listening on " + port());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.warn("Shutting down");
            } catch (LogServiceUnavailableException e) {
                System.err.println("Could not log shutdown");
            }
        }));
    }
}
