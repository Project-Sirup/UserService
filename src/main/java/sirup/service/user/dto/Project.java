package sirup.service.user.dto;

import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public record Project(String projectId, String projectName, String organisationId, Map<User, PermissionLevel> users) implements DTO {

    public Project(String projectName, String organisationId) {
        this(UUID.randomUUID().toString(), projectName, organisationId, new HashMap<>());
    }
    public Project(String projectId, String projectName, String organisationId) {
        this(projectId, projectName, organisationId, new HashMap<>());
    }

    @Override
    public String getId() {
        return projectId();
    }

    public static Project fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            String projectId = resultSet.getString("projectId");
            String projectName = resultSet.getString("projectName");
            String organisationId = resultSet.getString("organisationId");
            if (projectId == null || projectName == null || organisationId == null) {
                throw new CouldNotMakeResourceException("Could not make project from ResultSet");
            }
            return new Project(projectId, projectName, organisationId);
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException("Could not make project from ResultSet");
        }
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Project p = (Project) obj;
        return this.getId().equals(p.getId());
    }
}
