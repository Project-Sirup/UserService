package sirup.service.user.dto;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record Service(UUID serviceID, String name, File serviceFile, Map<User, PrivilegeLevel> users) {

    public Service(String name) {
        this(UUID.randomUUID(), name, null, new HashMap<>());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Service s = (Service) obj;
        return this.serviceID().equals(s.serviceID());
    }
}
