package sirup.service.user.controllers;

import com.google.api.Http;
import sirup.service.auth.rpc.client.AuthServiceUnavailableException;
import sirup.service.user.api.Context;
import sirup.service.user.dto.Invite;
import sirup.service.user.dto.OrganisationPermission;
import sirup.service.user.dto.PermissionLevel;
import sirup.service.user.dto.SystemAccess;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;
import sirup.service.user.util.ReturnObj;
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

public class InviteController extends AbstractController {
    public InviteController(Context context) {
        super(context);
    }

    private final HttpClient client = HttpClient.newBuilder().build();

    public Object store(Request request, Response response) {
        try {
            //TODO: access notification service
            StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
            this.invites.add(new Invite(storeRequest.senderId(), storeRequest.receiverId(), storeRequest.organisationId()));
            Invite invite = this.invites.get(storeRequest.receiverId(), storeRequest.senderId(), storeRequest.organisationId());
            try {
                notifyReceiver(invite);
            } catch (URISyntaxException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.CREATED));
        } catch (CouldNotMakeResourceException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.ALREADY_EXISTS, e.getMessage()));
        } catch (AuthServiceUnavailableException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.SERVICE_UNAVAILABLE, e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.DOES_NOT_EXIST, e.getMessage()));
        }
    }
    private record StoreRequest(String senderId, String receiverId, String organisationId) {}

    public Object response(Request request, Response response) {
        //TODO: access notification service
        try {
            ResponseRequest responseRequest = this.gson.fromJson(request.body(), ResponseRequest.class);
            // Checking to see if the request is from the receiver of the invite
            String token = request.headers("Token");
            int systemAccess = SystemAccess.fromString(request.headers("SystemAccess")).id;
            if (!this.authClient.auth(token, responseRequest.invite().receiverId(), systemAccess)) {
                return this.sendResponseAsJson(response, new ReturnObj<>(Status.UNAUTHORIZED));
            }
            if (responseRequest.accepted) {
                this.organisationPermissions.add(new OrganisationPermission(
                        responseRequest.invite().organisationId(),
                        responseRequest.invite().receiverId(),
                        PermissionLevel.VIEW));
                try {
                    notifySender(responseRequest.invite());
                } catch (URISyntaxException | IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.invites.delete(responseRequest.invite());
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        } catch (CouldNotMakeResourceException e) {
            return this.returnBadRequest(response, e.getMessage());
        }
    }
    private record ResponseRequest(Invite invite, boolean accepted) {};

    private void notifySender(Invite invite) throws URISyntaxException, IOException, InterruptedException {
        InviteNotificationObject ino = new InviteNotificationObject("invite", invite.senderId(), invite);
        HttpRequest request = HttpRequest.newBuilder()
                //TODO: add to .env
                .uri(new URI("http://127.0.0.1:2104/api/v1/trigger"))
                .setHeader("Content-Type", "Application/json")
                .POST(HttpRequest.BodyPublishers.ofString(this.gson.toJson(ino)))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    private record InviteNotificationObject(String eventType, String id, Object data) {}
    private void notifyReceiver(Invite invite) throws URISyntaxException, IOException, InterruptedException {
        //System.out.println("Notifying " + invite.receiverId());
        InviteNotificationObject ino = new InviteNotificationObject("invite", invite.receiverId(), invite);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://127.0.0.1:2104/api/v1/trigger"))
                .setHeader("Content-Type","Application/json")
                .POST(HttpRequest.BodyPublishers.ofString(this.gson.toJson(ino)))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    public Object findReceived(Request request, Response response) {
        try {
            String userId = request.headers("UserId");
            List<Invite> invites = this.invites.getAllReceived(userId);
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK, invites));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }

    public Object findSent(Request request, Response response) {
        try {
            String userId = request.headers("UserId");
            List<Invite> invites = this.invites.getAllSent(userId);
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK, invites));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }

    public Object remove(Request request, Response response) {
        try {
            DeleteRequest deleteRequest = this.gson.fromJson(request.body(), DeleteRequest.class);
            if (!this.invites.delete(deleteRequest.invite())) {
                return this.returnDoesNotExist(response);
            }
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.OK));
        }
        catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }
    private record DeleteRequest(Invite invite) {}
}
