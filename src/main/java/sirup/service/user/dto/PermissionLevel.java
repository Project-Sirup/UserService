package sirup.service.user.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

//TODO: Rename to permission
public enum PermissionLevel {
    NO_ACCESS(-1),
    VIEW(0),
    EDIT(1),
    MANAGER(2),
    ADMIN(3),
    OWNER(4);
    public final int id;
    PermissionLevel(final int id) {
        this.id = id;
    }
    public static PermissionLevel fromId(int id) {
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
    public static PermissionLevel fromResultSet(ResultSet resultSet) {
        try {
            return PermissionLevel.fromId(resultSet.getInt("permissionId"));
        } catch (SQLException e) {
            return NO_ACCESS;
        }
    }
}
