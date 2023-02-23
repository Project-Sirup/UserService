package sirup.service.user.dto;

import java.util.*;

public record Project(UUID projectID, String name, Set<Service> services, Map<User, PrivilegeLevel> assignedUsers) {
    public Project(String name) {
        this(UUID.randomUUID(),name,new HashSet<>(),new HashMap<>());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Project p = (Project) obj;
        return this.projectID().equals(p.projectID());
    }
}
