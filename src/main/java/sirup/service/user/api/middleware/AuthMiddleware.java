package sirup.service.user.api.middleware;

import sirup.service.auth.rpc.client.AuthClient;
import sirup.service.auth.rpc.client.AuthServiceUnavailableException;
import sirup.service.user.exceptions.ExternalServiceException;
import sirup.service.user.util.Status;
import spark.Filter;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class AuthMiddleware implements Filter {

    private static final AuthClient AUTH_CLIENT = AuthClient.getInstance();

    @Override
    public void handle(Request request, Response response) throws ExternalServiceException {
        String token = request.headers("Token");
        String userId = request.params("userId");
        if (userId == null) {
            halt(Status.BAD_REQUEST.getCode(), "Missing {userId} from url");
        }
        if (token == null || token.isEmpty()) {
            halt(Status.BAD_REQUEST.getCode(), "Missing {token} from header");
        }
        try {
            if (!AUTH_CLIENT.auth(token, userId)) {
                halt(Status.INVALID_TOKEN.getCode(), Status.INVALID_TOKEN.getMessage());
            }
        } catch (AuthServiceUnavailableException e) {
            halt(Status.SERVICE_UNAVAILABLE.getCode(), "AuthenticationService is unavailable");
        }
    }
}
