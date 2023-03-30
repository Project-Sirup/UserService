package sirup.service.user.controllers;

import sirup.service.user.api.Context;
import sirup.service.user.dto.PrivilegeLevel;
import sirup.service.user.dto.Project;
import sirup.service.user.dto.ProjectPermission;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

public class ProjectController extends AbstractController {

    public ProjectController(final Context context) {
        super(context);
    }

    public Object find(Request request, Response response) {

        return null;
    }

    public Object store(Request request, Response response) {
        StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
        Project project = new Project(storeRequest.projectName(), storeRequest.organisationId());
        this.projects.add(project);
        ProjectPermission projectPermission = new ProjectPermission(project.getId(), storeRequest.userId(), PrivilegeLevel.OWNER);
        this.projectPermissions.add(projectPermission);
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.CREATED, new StoreResponse(project.getId())));
    }
    private record StoreRequest(String projectName, String organisationId, String userId) {}
    private record StoreResponse(String projectId) {}

    public Object update(Request request, Response response) {
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.NOT_IMPLEMENTED));
    }

    public Object remove(Request request, Response response) {
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.NOT_IMPLEMENTED));
    }
}
