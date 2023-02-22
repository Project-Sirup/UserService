package sirup.service.user.database;

import java.sql.Connection;

public interface IDatabase {
    boolean connect();
    boolean disconnect();
    Connection getConnection();
}
