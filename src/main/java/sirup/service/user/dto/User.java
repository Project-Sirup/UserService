package sirup.service.user.dto;

import com.google.gson.Gson;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record User(String userId, String userName, String password, PrivilegeLevel privilegeLevel) implements DTO {

    private static final Gson GSON = new Gson();

    public User (String userName, String password) {
        this(UUID.randomUUID().toString(), userName, password, PrivilegeLevel.VIEW);
    }
    public User(String userId, String userName, String password) {
        this(userId, userName, password, PrivilegeLevel.VIEW);
    }
    public User(String userId, String userName, PrivilegeLevel privilegeLevel) {
        this(userId, userName, "", privilegeLevel);
    }

    @Override
    public String getId() {
        return userId();
    }

    public static User fromResultSetWithPass(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            return new User(
                    resultSet.getString("userId"),
                    resultSet.getString("userName"),
                    resultSet.getString("password"),
                    PrivilegeLevel.fromResultSet(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CouldNotMakeResourceException("Could not make user from ResultSet");
        }
    }

    public static User fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {
            return new User(
                    resultSet.getString("userId"),
                    resultSet.getString("userName"),
                    PrivilegeLevel.fromResultSet(resultSet));
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException("Could not make user from ResultSet");
        }
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        User u = (User) obj;
        return this.getId().equals(u.getId());
    }
}
