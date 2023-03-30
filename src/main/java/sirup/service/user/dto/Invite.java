package sirup.service.user.dto;

import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public record Invite(String senderId, String receiverId, String organisationId) implements DTO {

    public static Invite fromResultSet(ResultSet resultSet) throws SQLException {
        return new Invite(
                resultSet.getString("senderId"),
                resultSet.getString("receiverId"),
                resultSet.getString("organisationId")
        );
    }

    @Override
    public String getId() {
        return senderId() + receiverId() + organisationId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Invite i = (Invite) obj;
        return this.getId().equals(i.getId());
    }
}
