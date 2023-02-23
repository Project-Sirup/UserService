package sirup.service.user.controllers;

import com.google.gson.Gson;
import sirup.service.user.api.rpc.SirupAuthClient;
import sirup.service.user.util.SirupLogger;
import sirup.service.user.util.Status;
import spark.Response;

import java.sql.Connection;

public abstract class AbstractController {
    protected final Connection connection;
    protected final Gson gson;
    protected final SirupLogger logger;
    protected final SirupAuthClient authClient;
    public AbstractController(final Connection connection) {
        this.connection = connection;
        this.logger = SirupLogger.getInstance();
        this.gson = new Gson();
        this.authClient = SirupAuthClient.getInstance();
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
