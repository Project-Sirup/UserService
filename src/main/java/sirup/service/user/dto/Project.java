package sirup.service.user.dto;

import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public record Project(UUID projectId, String projectName, UUID organisationId, Set<Microservice> microservices, Set<User> users) implements DTO {

    public Project(String projectName, UUID organisationId) {
        this(UUID.randomUUID(),projectName,organisationId,new HashSet<>(),new HashSet<>());
    }
    public Project(UUID projectId, String projectName, UUID organisationId) {
        this(projectId, projectName, organisationId, new HashSet<>(), new HashSet<>());
    }

    @Override
    public String getId() {
        return projectId().toString();
    }

    public static Project fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            return new Project(
                    UUID.fromString(resultSet.getString("projectId")),
                    resultSet.getString("projectName"),
                    UUID.fromString(resultSet.getString("organisationId")));
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
        return this.projectId().equals(p.projectId());
    }
}
