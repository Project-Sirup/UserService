package sirup.service.user.dto;

import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public record Microservice(String microserviceId, String microserviceName, String projectId, Object serviceFile, Set<User> users) implements DTO {

    public Microservice(String serviceName, String projectId) {
        this(UUID.randomUUID().toString(), serviceName, projectId, null, new HashSet<>());
    }
    public Microservice(String serviceId, String serviceName, String projectId) {
        this(serviceId, serviceName, projectId, null, new HashSet<>());
    }

    @Override
    public String getId() {
        return microserviceId().toString();
    }

    public static Microservice fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            return new Microservice(
                    resultSet.getString("microserviceID"),
                    resultSet.getString("microserviceName"),
                    resultSet.getString("projectId"));
        } catch (NullPointerException | SQLException e) {
            throw new CouldNotMakeResourceException("Could not make microservice from ResultSet");
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
        Microservice m = (Microservice) obj;
        return this.getId().equals(m.getId());
    }
}
