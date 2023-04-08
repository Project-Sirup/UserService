package sirup.service.user.services;

import sirup.service.user.dto.MicroservicePermission;
import sirup.service.user.exceptions.CouldNotMakeResourceException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MicroservicePermissionService extends AbstractService<MicroservicePermission> {

    public String add(MicroservicePermission microservicePermission) throws CouldNotMakeResourceException {
        try {
            String insetQuery = "INSERT INTO microservicepermissions (userid, microserviceid, permissionid) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = this.connection.prepareStatement(insetQuery);
            insertStatement.setString(1, microservicePermission.userId());
            insertStatement.setString(2, microservicePermission.microserviceId());
            insertStatement.setInt(3, microservicePermission.permissionLevel().id);
            insertStatement.execute();
            return "";
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException("Could not create Invite");
        }
    }
}
