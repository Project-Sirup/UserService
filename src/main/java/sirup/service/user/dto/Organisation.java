package sirup.service.user.dto;

import com.google.gson.Gson;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public record Organisation(String organisationId, String organisationName, Map<User, PermissionLevel> users) implements DTO {

    public Organisation(String organisationName) {
        this(UUID.randomUUID().toString(), organisationName, new HashMap<>());
    }
    public Organisation(String organisationId, String organisationName) {
        this(organisationId, organisationName, new HashMap<>());
    }

    @Override
    public String getId() {
        return organisationId();
    }

    public static Organisation fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            String organisationId = resultSet.getString("organisationId");
            String organisationName = resultSet.getString("organisationName");
            if (organisationId == null || organisationName == null) {
                throw new CouldNotMakeResourceException("Could not make organisation from ResultSet");
            }
            return new Organisation(organisationId, organisationName);
        } catch (SQLException e) {
           throw new CouldNotMakeResourceException("Could not make organisation from ResultSet");
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
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
        Organisation o = (Organisation) obj;
        return this.getId().equals(o.getId());
    }
}
