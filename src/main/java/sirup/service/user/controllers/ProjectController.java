package sirup.service.user.controllers;

import sirup.service.user.api.Context;
import sirup.service.user.dto.PermissionLevel;
import sirup.service.user.dto.Project;
import sirup.service.user.dto.ProjectPermission;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;
import sirup.service.user.util.ReturnObj;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

import java.util.List;

public class ProjectController extends AbstractController {

    public ProjectController(final Context context) {
        super(context);
    }

    public Object find(Request request, Response response) {
        try {
            String projectId = request.params("projectId");
            Project project = this.projects.get(projectId);
            return this.sendResponseAsJson(response, new ReturnObj<>("Project found", project));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }

    public Object findAll(Request request, Response response) {
        try {
            String organisationId = request.params("organisationId");
            List<Project> projects = this.projects.getAll(organisationId);
            return this.sendResponseAsJson(response, new ReturnObj<>("Projects found", projects));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }

    public Object store(Request request, Response response) {
        try {
            StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
            String userId = request.headers("UserID");
            Project project = new Project(storeRequest.projectName(), storeRequest.organisationId());
            this.projects.add(project);
            ProjectPermission projectPermission = new ProjectPermission(
                    project.getId(),
                    userId,
                    PermissionLevel.OWNER);
            this.projectPermissions.add(projectPermission);
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.CREATED, "Project created", project));
        } catch (CouldNotMakeResourceException e) {
            return this.returnBadRequest(response, e.getMessage());
        }
    }
    private record StoreRequest(String projectName, String organisationId) {}

    public Object update(Request request, Response response) {
        try {
            UpdateRequest updateRequest = this.gson.fromJson(request.body(), UpdateRequest.class);
            String projectId = request.params("projectId");
            Project project = new Project(projectId, updateRequest.projectName());
            if (!this.projects.update(project)) {
                return this.returnDoesNotExist(response);
            }
            return this.sendResponseAsJson(response, new ReturnObj<>("Project updated", project));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }
    private record UpdateRequest(String projectName) {}

    public Object remove(Request request, Response response) {
        try {
            String projectId = request.params("projectId");
            if (this.projects.delete(projectId)) {
                return this.returnDoesNotExist(response);
            }
            return this.sendResponseAsJson(response, new ReturnObj<>( "Project deleted"));
        } catch (ResourceNotFoundException e) {
            return this.returnDoesNotExist(response, e.getMessage());
        }
    }
}
