package sirup.service.user.api;

import sirup.service.user.database.IDatabase;
import sirup.service.user.interfaces.DTO;
import sirup.service.user.services.AbstractService;
import sirup.service.user.util.SirupLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Context {

    private IDatabase database;
    private final Map<Class<? extends DTO>, AbstractService<? extends DTO>> serviceMap;

    private Context() {
        this.serviceMap = new HashMap<>();
    }

    private void setDatabase(IDatabase database) {
        this.database = database;
    }
    public IDatabase getDatabase() {
        return this.database;
    }

    private void addService(Class<? extends DTO> clazz, AbstractService<? extends DTO> service) {
        this.serviceMap.put(clazz, service);
    }
    public AbstractService<? extends DTO> getService(Class<? extends DTO> clazz) {
        return this.serviceMap.get(clazz);
    }
    public Collection<AbstractService<? extends DTO>> getServices() {
        return this.serviceMap.values();
    }

    public static ContextBuilder builder() {
        return new ContextBuilder(new Context());
    }
    public static class ContextBuilder {
        private final Context context;
        private ContextBuilder(final Context context) {
            this.context = context;
        }

        public ContextBuilder database(IDatabase database) {
            this.context.setDatabase(database);
            return this;
        }

        public ContextBuilder addService(Class<? extends DTO> clazz, AbstractService<? extends DTO> service) {
            this.context.addService(clazz, service);
            return this;
        }

        public Context build() {
            this.context.getServices().forEach(service -> service.setContext(context));
            return this.context;
        }
    }
}
