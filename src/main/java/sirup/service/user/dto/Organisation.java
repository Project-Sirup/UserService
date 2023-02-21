package sirup.service.user.dto;

import java.util.*;

public record Organisation(UUID id, Set<Project> projects, Map<User, PrivilegeLevel> users) {
    public Organisation() {
        this(UUID.randomUUID(), new HashSet<>(), new HashMap<>());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Organisation p = (Organisation) obj;
        return this.id().equals(p.id());
    }
}
