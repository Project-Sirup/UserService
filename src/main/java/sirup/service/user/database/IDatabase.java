package sirup.service.user.database;

public interface IDatabase {
    void setConnectionString(String connectionString);
    boolean connect();
    boolean disconnect();
}
