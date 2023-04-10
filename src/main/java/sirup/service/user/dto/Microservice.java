package sirup.service.user.dto;

import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public record Microservice(String microserviceId, String microserviceName, String projectId, Object microserviceFile, Set<User> users) implements DTO {

    public Microservice(String microserviceName, String projectId) {
        this(UUID.randomUUID().toString(), microserviceName, projectId, null, new HashSet<>());
    }
    public Microservice(String microserviceId, String microserviceName, String projectId) {
        this(microserviceId, microserviceName, projectId, null, new HashSet<>());
    }
    public Microservice(String microserviceId, String microserviceName, String projectId, Object microserviceFile) {
        this(microserviceId, microserviceName, projectId, microserviceFile, new HashSet<>());
    }

    @Override
    public String getId() {
        return microserviceId();
    }

    public static Microservice fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            String microserviceId = resultSet.getString("microserviceId");
            String microserviceName = resultSet.getString("microserviceName");
            Object microserviceFile = resultSet.getObject("microserviceFile");
            String projectId = resultSet.getString("projectId");
            if (microserviceId == null || microserviceName == null || projectId == null) {
                throw new CouldNotMakeResourceException("Could not make microservice from ResultSet");
            }
            return new Microservice(microserviceId, microserviceName, projectId, microserviceFile);
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException("Could not make microservice from ResultSet");
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
        Microservice m = (Microservice) obj;
        return this.getId().equals(m.getId());
    }
}
