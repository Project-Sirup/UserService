package sirup.service.user.controllers;

import sirup.service.user.dto.Organisation;
import sirup.service.user.dto.PrivilegeLevel;
import sirup.service.user.dto.User;
import sirup.service.user.util.Result;
import sirup.service.user.util.Status;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class OrganisationController extends AbstractController {


    public OrganisationController(Connection connection) {
        super(connection);
    }

    public Object store(Request request, Response response) {
        StoreRequest storeRequest = this.gson.fromJson(request.body(), StoreRequest.class);
        //auth request
        if (storeRequest.token().isEmpty()) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.BAD_REQUEST));
        }
        Result<String, Boolean> result = this.authClient.auth(storeRequest.token());
        if (result.failed()) {
            return this.sendResponseAsJson(response, new ReturnObj<>(Status.INVALID_TOKEN));
        }

        //Create new org in db
        UUID id = UUID.randomUUID();

        try {
            String insertQuery = "INSERT INTO sirupuser.public.organisations (organisationid, organisationname) VALUES (?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, id.toString());
            insertStatement.setString(2, storeRequest.organisation().name());
            insertStatement.execute();

            //Add user to org_perms with admin perm
            String permissionQuery = "INSERT INTO organisationpermissions (userid, organisationid, permissionid) VALUES (?, ?, ?);";
            PreparedStatement permissionStatement = this.connection.prepareStatement(permissionQuery);
            permissionStatement.setString(1, storeRequest.userID);
            permissionStatement.setString(2, id.toString());
            permissionStatement.setInt(3, PrivilegeLevel.ADMIN.id);
            permissionStatement.execute();

            String selectQuery = "SELECT * FROM organisationpermissions op " +
                    "INNER JOIN users u on op.userid = u.userid " +
                    "INNER JOIN organisations o on op.organisationid = o.organisationid " +
                    "WHERE op.organisationID = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id.toString());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            UUID oid = UUID.fromString(resultSet.getString("organisationID"));
            String name = resultSet.getString("organisationName");
            Organisation organisation = new Organisation(oid, name);
            UUID uid = UUID.fromString(resultSet.getString("userID"));
            String uName = resultSet.getString("userName");
            User user = new User(uid, uName);
            organisation.users().put(user, PrivilegeLevel.ADMIN);
            logger.info("+++ Created org with id = " + id);
            return this.sendResponseAsJson(response, new ReturnObj<>("Created new organisation", organisation));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.BAD_REQUEST));
    }
    private record StoreRequest(String userID, String token, Organisation organisation) {}

    public Object addUser(Request request, Response response) {
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.NOT_IMPLEMENTED));
    }

    public Object update(Request request, Response response) {
        System.out.println("UPDATE");
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.NOT_IMPLEMENTED));
    }

    public Object remove(Request request, Response response) {
        System.out.println("REMOVE");
        return this.sendResponseAsJson(response, new ReturnObj<>(Status.NOT_IMPLEMENTED));
    }
}
