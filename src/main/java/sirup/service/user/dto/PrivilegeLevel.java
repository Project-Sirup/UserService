package sirup.service.user.dto;

public enum PrivilegeLevel {
    ADMIN(0),
    MANAGER(1),
    DEFAULT(2);
    public final int id;
    PrivilegeLevel(final int id) {
        this.id = id;
    }
}
