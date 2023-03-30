package sirup.service.user.controllers;

import sirup.service.auth.rpc.client.AuthServiceUnavailableException;
import sirup.service.user.api.Context;
import sirup.service.user.dto.Invite;
import sirup.service.user.dto.OrganisationPermission;
import sirup.service.user.dto.PrivilegeLevel;
import sirup.service.user.dto.User;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static sirup.service.log.rpc.client.ColorUtil.action;
import static sirup.service.log.rpc.client.ColorUtil.id;

public class InviteController extends AbstractController {
    public InviteController(Context context) {
        super(context);
    }

    public Object store(Request request, Response response) {
        try {
            //TODO: access notification service
            StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
            this.invites.add(storeRequest.invite());
            logger.info(this.getClass().getSimpleName() + " -> " + id(storeRequest.invite().getId()) + " -> " + action("created"));
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.CREATED));
        } catch (CouldNotMakeResourceException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.ALREADY_EXISTS, e.getMessage()));
        } catch (AuthServiceUnavailableException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.SERVICE_UNAVAILABLE, "AuthenticationService is unavailable"));
        }
    }
    private record StoreRequest(Invite invite) {}

    public Object response(Request request, Response response) {
        //TODO: access notification service
        ResponseRequest responseRequest = this.gson.fromJson(request.body(), ResponseRequest.class);
        String token = request.headers("Token");
        if (!this.authClient.auth(token, responseRequest.invite().receiverId())) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.UNAUTHORIZED));
        }
        if (responseRequest.accepted) {
            this.organisationPermissions.add(new OrganisationPermission(
                    responseRequest.invite().organisationId(),
                    responseRequest.invite().receiverId(),
                    PrivilegeLevel.VIEW));
            try {
                notifySender(responseRequest.invite());
            } catch (URISyntaxException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.invites.delete(responseRequest.invite());
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK));
    }
    private record ResponseRequest(Invite invite, boolean accepted) {};

    private void notifySender(Invite invite) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                //TODO: add to .env
                .uri(new URI("http://127.0.0.1:2300/trigger/" + invite.senderId()))
                .setHeader("Content-Type", "Application/json")
                .POST(HttpRequest.BodyPublishers.ofString(this.gson.toJson(invite)))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    public Object find(Request request, Response response) {
        String userId = request.headers("UserId");
        List<Invite> invites = this.invites.getAll(userId);
        logger.info(this.getClass().getSimpleName() + " -> " + id(userId) + " -> " + action("found"));
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK, new FindResponse(invites)));
    }
    private record FindResponse(List<Invite> invites) {}


    public Object remove(Request request, Response response) {
        DeleteRequest deleteRequest = this.gson.fromJson(request.body(), DeleteRequest.class);
        if (!this.invites.delete(deleteRequest.invite())) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST));
        }
        logger.info(this.getClass().getSimpleName() + " -> " + id(deleteRequest.invite().getId()) + " -> " + action("deleted"));
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK));
    }
    private record DeleteRequest(Invite invite) {}
}
