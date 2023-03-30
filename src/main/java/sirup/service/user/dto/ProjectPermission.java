package sirup.service.user.dto;

import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public record ProjectPermission(String projectId, String userId, PrivilegeLevel privilegeLevel) implements DTO {

    public static ProjectPermission fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            return new ProjectPermission(
                    resultSet.getString("projectId"),
                    resultSet.getString("userId"),
                    PrivilegeLevel.fromResultSet(resultSet)
            );
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException("Could not make ProjectPermission");
        }
    }

    @Override
    public String getId() {
        return projectId() + userId() + privilegeLevel().id;
    }
}
