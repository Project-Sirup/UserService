package sirup.service.user.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sirup.service.log.rpc.client.LogClient;
import sirup.service.user.api.Context;

import java.sql.Connection;

public abstract class AbstractService<T> {
    protected Context context;
    protected Connection connection;
    protected LogClient logger;
    protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void setContext(Context context) {
        this.context = context;
    }
    public void init() {
        this.connection = this.context.getDatabase().getConnection();
        this.logger = LogClient.getInstance();
    }
}
