package sirup.service.user.api.middleware;

import com.google.gson.Gson;
import sirup.service.auth.rpc.client.AuthClient;
import sirup.service.auth.rpc.client.AuthServiceUnavailableException;
import sirup.service.log.rpc.client.LogClient;
import sirup.service.user.dto.SystemAccess;
import sirup.service.user.util.ReturnObj;
import sirup.service.user.util.Status;
import spark.Filter;
import spark.Request;
import spark.Response;

import static sirup.service.log.rpc.client.ColorUtil.*;
import static spark.Spark.*;

public class AuthMiddleware implements Filter {

    private static final AuthClient AUTH_CLIENT = AuthClient.getInstance();
    private final LogClient logger = LogClient.getInstance();
    private final Gson gson = new Gson();

    @Override
    public void handle(Request request, Response response) {
        if (request.requestMethod().equals("OPTIONS")) {
            return;
        }
        String token = request.headers("Token");
        String userId = request.headers("UserId");
        SystemAccess systemAccess = SystemAccess.DEFAULT;
        try {
            systemAccess = SystemAccess.fromString(request.headers("SystemAccess"));
        } catch (NumberFormatException e) {
            halt(Status.BAD_REQUEST.getCode(), this.gson.toJson(new ReturnObj<>(Status.BAD_REQUEST,"Missing {SystemAccess} from header")));
        }
        if (userId == null) {
            halt(Status.BAD_REQUEST.getCode(), this.gson.toJson(new ReturnObj<>(Status.BAD_REQUEST,"Missing {UserId} from header")));
        }
        if (token == null || token.isEmpty()) {
            halt(Status.BAD_REQUEST.getCode(), this.gson.toJson(new ReturnObj<>(Status.BAD_REQUEST,"Missing {Token} from header")));
        }
        try {
            if (!AUTH_CLIENT.auth(token, userId, systemAccess.id)) {
                logger.warn("Failed auth by " + id(userId));
                halt(Status.INVALID_TOKEN.getCode(), this.gson.toJson(new ReturnObj<>(Status.INVALID_TOKEN)));
            }
        } catch (AuthServiceUnavailableException e) {
            halt(Status.SERVICE_UNAVAILABLE.getCode(), this.gson.toJson(new ReturnObj<>(Status.SERVICE_UNAVAILABLE,"AuthenticationService is unavailable")));
        }
    }
}
