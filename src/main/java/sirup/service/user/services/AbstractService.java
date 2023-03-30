package sirup.service.user.services;

import sirup.service.log.rpc.client.LogClient;
import sirup.service.user.api.Context;

import java.sql.Connection;

public abstract class AbstractService<T> {
    protected Context context;
    protected Connection connection;
    protected LogClient logger;

    public void setContext(Context context) {
        this.context = context;
    }
    public void init() {
        this.connection = this.context.getDatabase().getConnection();
        this.logger = LogClient.getInstance();
    }
}
