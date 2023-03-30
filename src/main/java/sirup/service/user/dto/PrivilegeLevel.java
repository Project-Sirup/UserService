package sirup.service.user.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

//TODO: Rename to permission
public enum PrivilegeLevel {
    NO_ACCESS(-1),
    VIEW(0),
    EDIT(1),
    MANAGER(2),
    ADMIN(3),
    OWNER(4);
    public final int id;
    PrivilegeLevel(final int id) {
        this.id = id;
    }
    public static PrivilegeLevel fromId(int id) {
        switch (id) {
            default -> {
                return NO_ACCESS;
            }
            case 0 -> {
                return VIEW;
            }
            case 1 -> {
                return EDIT;
            }
            case 2 -> {
                return MANAGER;
            }
            case 3 -> {
                return ADMIN;
            }
            case 4 -> {
                return OWNER;
            }
        }
    }
    public static PrivilegeLevel fromResultSet(ResultSet resultSet) {
        try {
            return PrivilegeLevel.fromId(resultSet.getInt("permissionID"));
        } catch (SQLException e) {
            return PrivilegeLevel.NO_ACCESS;
        }
    }
}
