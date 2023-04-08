package sirup.service.user.services;

import sirup.service.user.dto.ProjectPermission;
import sirup.service.user.exceptions.CouldNotMakeResourceException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProjectPermissionService extends AbstractService<ProjectPermission> {

    public String add(ProjectPermission projectPermission) throws CouldNotMakeResourceException {
        try {
            String insertQuery = "INSERT INTO projectpermissions (userid, projectid, permissionid) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, projectPermission.userId());
            insertStatement.setString(2, projectPermission.projectId());
            insertStatement.setInt(3, projectPermission.permissionLevel().id);
            insertStatement.execute();
            return "";
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

}
