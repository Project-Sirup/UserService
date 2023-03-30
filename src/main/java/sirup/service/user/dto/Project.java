package sirup.service.user.dto;

import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public record Project(String projectId, String projectName, String organisationId, Set<Microservice> microservices, Set<User> users) implements DTO {

    public Project(String projectName, String organisationId) {
        this(UUID.randomUUID().toString(), projectName, organisationId, new HashSet<>(), new HashSet<>());
    }
    public Project(String projectId, String projectName, String organisationId) {
        this(projectId, projectName, organisationId, new HashSet<>(), new HashSet<>());
    }

    @Override
    public String getId() {
        return projectId().toString();
    }

    public static Project fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            return new Project(
                    resultSet.getString("projectId"),
                    resultSet.getString("projectName"),
                    resultSet.getString("organisationId"));
        } catch (NullPointerException | SQLException e) {
            throw new CouldNotMakeResourceException("Could not make project from ResultSet");
        }
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
