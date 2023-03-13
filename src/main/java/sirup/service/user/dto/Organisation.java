package sirup.service.user.dto;

import com.google.gson.Gson;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public record Organisation(UUID organisationId, String organisationName, Set<Project> projects, Set<User> users) implements DTO {

    public Organisation(String organisationName) {
        this(UUID.randomUUID(), organisationName, new HashSet<>(), new HashSet<>());
    }
    public Organisation(UUID organisationId, String organisationName) {
        this(organisationId, organisationName, new HashSet<>(), new HashSet<>());
    }

    @Override
    public String getId() {
        return organisationId().toString();
    }

    public static Organisation fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            return new Organisation(
                    UUID.fromString(resultSet.getString("organisationID")),
                    resultSet.getString("organisationName"));
        } catch (NullPointerException | SQLException e) {
           throw new CouldNotMakeResourceException("Could not make organisation from ResultSet");
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Organisation o = (Organisation) obj;
        return this.organisationId().equals(o.organisationId());
    }
}
