package sirup.service.user.database;

public class PostgreSQL implements IDatabase {

    @Override
    public void setConnectionString(String connectionString) {

    }

    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public boolean disconnect() {
        return false;
    }
}
