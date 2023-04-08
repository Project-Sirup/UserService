package sirup.service.user.dto;

import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public record MicroservicePermission(String microserviceId, String userId, PermissionLevel permissionLevel) implements DTO {

    public static MicroservicePermission fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            return new MicroservicePermission(
                    resultSet.getString("microserviceId"),
                    resultSet.getString("userId"),
                    PermissionLevel.fromResultSet(resultSet)
            );
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException("Could not make MicroservicePermission from ResultSet");
        }
    }

    @Override
    public String getId() {
        return microserviceId() + userId() + permissionLevel().id;
    }
}
