package sirup.service.user.dto;

import java.util.*;

public record Organisation(UUID organisationID, String name, Set<Project> projects, Map<User, PrivilegeLevel> users) {
    public Organisation(String name) {
        this(UUID.randomUUID(), name, new HashSet<>(), new HashMap<>());
    }

    public Organisation(UUID organisationID, String name) {
        this(organisationID, name, new HashSet<>(), new HashMap<>());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Organisation o = (Organisation) obj;
        return this.organisationID().equals(o.organisationID());
    }
}
