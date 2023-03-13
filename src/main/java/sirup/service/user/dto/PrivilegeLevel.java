package sirup.service.user.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

//TODO: Rename to permission
public enum PrivilegeLevel {
    DEFAULT(0),
    MANAGER(1),
    ADMIN(2),
    OWNER(3);
    public final int id;
    PrivilegeLevel(final int id) {
        this.id = id;
    }
    public static PrivilegeLevel fromId(int id) {
        switch (id) {
            default -> {
                return DEFAULT;
            }
            case 1 -> {
                return MANAGER;
            }
            case 2 -> {
                return ADMIN;
            }
            case 3 -> {
                return OWNER;
            }
        }
    }
    public static PrivilegeLevel fromResultSet(ResultSet resultSet) {
        try {
            return PrivilegeLevel.fromId(resultSet.getInt("permissionID"));
        } catch (SQLException e) {
            return PrivilegeLevel.DEFAULT;
        }
    }
}
