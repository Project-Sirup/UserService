package sirup.service.user.services;

import sirup.service.user.dto.OrganisationPermission;
import sirup.service.user.exceptions.CouldNotMakeResourceException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrganisationPermissionService extends AbstractService<OrganisationPermission> {

    public String add(OrganisationPermission organisationPermission) throws CouldNotMakeResourceException {
        try {
            String insertQuery = "INSERT INTO organisationpermissions (userid, organisationid, permissionid) VALUES (?, ?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, organisationPermission.userId());
            insertStatement.setString(2, organisationPermission.organisationId());
            insertStatement.setInt(3, organisationPermission.permissionLevel().id);
            insertStatement.execute();
            return "";
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }
    public boolean update(OrganisationPermission organisationPermission) {
        throw new RuntimeException("Not implemented");
    }

    public boolean delete(String id) {
        throw new RuntimeException("Not implemented");
    }
}
