package sirup.service.user.dto;

import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public record OrganisationPermission(String organisationId, String userId, PrivilegeLevel privilegeLevel) implements DTO {

    public static OrganisationPermission fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            return new OrganisationPermission(
                    resultSet.getString("organisationId"),
                    resultSet.getString("userId"),
                    PrivilegeLevel.fromResultSet(resultSet)
            );
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException("Could not make OrganisationPermission");
        }
    }

    @Override
    public String getId() {
        return organisationId() + userId() + privilegeLevel().id;
    }
}
