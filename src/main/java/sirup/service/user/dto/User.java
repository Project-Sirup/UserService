package sirup.service.user.dto;

import com.google.gson.Gson;

import java.util.UUID;

public record User(UUID userID, String userName, String password) {

    private static final Gson GSON = new Gson();

    public User(String username, String password) {
        this(UUID.randomUUID(),username,password);
    }
    public User(UUID userID, String userName) {
        this(userID, userName, "");
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        User u = (User) obj;
        return this.userID().equals(u.userID());
    }
}
