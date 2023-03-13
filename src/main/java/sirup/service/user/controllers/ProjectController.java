package sirup.service.user.controllers;

import sirup.service.user.api.Context;
import sirup.service.user.dto.PrivilegeLevel;
import sirup.service.user.dto.Project;
import sirup.service.user.dto.User;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ProjectController extends AbstractController {

    public ProjectController(final Context context) {
        super(context);
    }

    public Object store(Request request, Response response) {
        StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
        //Auth
        //Create project in db
        UUID id = UUID.randomUUID();
        try {
            String insertQuery = "INSERT INTO projects (projectid, projectname, organisationid) VALUES (?, ?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, id.toString());
            insertStatement.setString(2, storeRequest.projectName());
            insertStatement.setString(3, storeRequest.organisationId());
            insertStatement.execute();

            //Add user perm to project
            String permissionQuery = "INSERT INTO projectpermissions (userid, projectid, permissionid) VALUES (?, ?, ?);";
            PreparedStatement permissionStatement = this.connection.prepareStatement(permissionQuery);
            permissionStatement.setString(1, storeRequest.userId());
            permissionStatement.setString(2, id.toString());
            permissionStatement.setInt(3, PrivilegeLevel.ADMIN.id);
            permissionStatement.execute();

            String selectQuery = "SELECT * FROM projectpermissions pp " +
                    "INNER JOIN users u on pp.userid = u.userid " +
                    "INNER JOIN projects p on pp.projectid = p.projectid " +
                    "WHERE pp.projectid = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id.toString());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            UUID pid = UUID.fromString(resultSet.getString("projectID"));
            String name = resultSet.getString("projectName");
            Project project = new Project(pid, name, UUID.fromString(storeRequest.organisationId()));
            UUID uid = UUID.fromString(resultSet.getString("userID"));
            String uName = resultSet.getString("userName");
            User user = new User(uid, uName, PrivilegeLevel.ADMIN);
            project.users().add(user);
            logger.info("+++ Created pro with id = " + id);
            return this.sendResponseAsJson(response, new ReturnObj<>("Created new project", project));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.sendResponseAsJson(response, new ReturnObj<>(Status.BAD_REQUEST));
    }
    private record StoreRequest(String token, String projectName, String organisationId, String userId) {}

    public Object update(Request request, Response response) {
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.NOT_IMPLEMENTED));
    }

    public Object remove(Request request, Response response) {
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.NOT_IMPLEMENTED));
    }
}
