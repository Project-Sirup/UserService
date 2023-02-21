package sirup.service.user.dto;

import java.util.UUID;

public record User(UUID id, String username, String password) {

    public User(String username, String password) {
        this(UUID.randomUUID(),username,password);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        User p = (User) obj;
        return this.id().equals(p.id());
    }
}
