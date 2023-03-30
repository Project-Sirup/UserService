package sirup.service.user.services;

import sirup.service.user.dto.OrganisationPermission;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrganisationPermissionService extends AbstractService<OrganisationPermission> {

    public String add(OrganisationPermission organisationPermission) {
        try {
            String insertQuery = "INSERT INTO organisationpermissions (userid, organisationid, permissionid) VALUES (?, ?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, organisationPermission.userId());
            insertStatement.setString(2, organisationPermission.organisationId());
            insertStatement.setInt(3, organisationPermission.privilegeLevel().id);
            insertStatement.execute();
            return "";
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

    public OrganisationPermission get(String id) {
        throw new RuntimeException("Not implemented");
    }

    public List<OrganisationPermission> getAll(String id) {
        try {
            String selectQuery = "SELECT * FROM organisationpermissions WHERE userId = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            List<OrganisationPermission> organisationPermissions = new ArrayList<>();
            while (resultSet.next()) {
                organisationPermissions.add(OrganisationPermission.fromResultSet(resultSet));
            }
            return organisationPermissions;
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public boolean update(OrganisationPermission organisationPermission) {
        throw new RuntimeException("Not implemented");
    }

    public boolean delete(String id) {
        throw new RuntimeException("Not implemented");
    }
}
