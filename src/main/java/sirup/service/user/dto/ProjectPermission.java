package sirup.service.user.dto;

import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public record ProjectPermission(String projectId, String userId, PermissionLevel permissionLevel) implements DTO {

    public static ProjectPermission fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            return new ProjectPermission(
                    resultSet.getString("projectId"),
                    resultSet.getString("userId"),
                    PermissionLevel.fromResultSet(resultSet)
            );
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException("Could not make ProjectPermission from ResultSet");
        }
    }

    @Override
    public String getId() {
        return projectId() + userId() + permissionLevel().id;
    }
}
