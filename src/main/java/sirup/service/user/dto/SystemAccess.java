package sirup.service.user.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

public enum SystemAccess {
    HIDDEN(-1),
    DEFAULT(0),
    PRO(1),
    ADMIN(2);
    public final int id;
    SystemAccess(final int id) {
        this.id = id;
    }
    public static SystemAccess fromId(int id) {
        switch (id) {
            default -> {
                return HIDDEN;
            }
            case 0 -> {
                return DEFAULT;
            }
            case 1 -> {
                return PRO;
            }
            case 2 -> {
                return ADMIN;
            }
        }
    }
    public static SystemAccess fromResultSet(ResultSet resultSet) {
        try {
            return SystemAccess.fromId(resultSet.getInt("systemAccess"));
        } catch (SQLException e) {
            return HIDDEN;
        }
    }
    public static SystemAccess fromString(String string) {
        string = string.toLowerCase();
        switch (string) {
            default -> {
                return HIDDEN;
            }
            case "default" -> {
                return DEFAULT;
            }
            case "pro" -> {
                return PRO;
            }
            case "admin" -> {
                return ADMIN;
            }
        }
    }
}
