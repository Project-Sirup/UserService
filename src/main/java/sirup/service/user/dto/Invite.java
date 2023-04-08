package sirup.service.user.dto;

import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.interfaces.DTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public record Invite(String senderId, String senderName, String receiverId, String receiverName, String organisationId, String organisationName) implements DTO {

    public Invite(String senderId, String receiverId, String organisationId) {
        this(senderId,"",receiverId,"",organisationId,"");
    }

    public static Invite fromResultSet(ResultSet resultSet) throws CouldNotMakeResourceException {
        try {

            return new Invite(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6)
            );
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException("Could not make Invite from ResultSet " + e.getMessage());
        }
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
