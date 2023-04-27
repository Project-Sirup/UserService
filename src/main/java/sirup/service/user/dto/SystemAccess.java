package sirup.service.user.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

public enum SystemAccess {
    HIDDEN(-1),
    DEFAULT(0),
    SERVICE(1),
    PRO(2),
    ADMIN(3);
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
                return SERVICE;
            }
            case 2 -> {
                return PRO;
            }
            case 3 -> {
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
