package sirup.service.user.dto;

import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public record Microservice(UUID microserviceId, String microserviceName, UUID projectId, Object serviceFile, Set<User> users) implements DTO {

    public Microservice(String serviceName, UUID projectId) {
        this(UUID.randomUUID(), serviceName, projectId, null, new HashSet<>());
    }
    public Microservice(UUID serviceId, String serviceName, UUID projectId) {
        this(serviceId, serviceName, projectId, null, new HashSet<>());
    }

    @Override
    public String getId() {
        return microserviceId().toString();
    }

    public static Microservice fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            return new Microservice(
                    UUID.fromString(resultSet.getString("microserviceID")),
                    resultSet.getString("microserviceName"),
                    UUID.fromString(resultSet.getString("projectId")));
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
        Microservice s = (Microservice) obj;
        return this.microserviceId().equals(s.microserviceId());
    }
}
