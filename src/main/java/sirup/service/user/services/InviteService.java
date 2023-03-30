package sirup.service.user.services;

import sirup.service.user.dto.Invite;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InviteService extends AbstractService<Invite> {

    public String add(Invite invite) {
        try {
            String insertQuery =
                    "INSERT INTO organisationinvites (senderid, receiverid, organisationid)" +
                    "SELECT * FROM (" +
                            "VALUES (?, ?, ?)) AS oS(senderId, receiverId, organisationId) WHERE NOT EXISTS (" +
                            "SELECT * FROM organisationpermissions oP " +
                            "WHERE oP.organisationid = ? AND oP.userid = ?);";

            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, invite.senderId());
            insertStatement.setString(2, invite.receiverId());
            insertStatement.setString(3, invite.organisationId());
            insertStatement.setString(4, invite.organisationId());
            insertStatement.setString(5, invite.receiverId());
            insertStatement.execute();
            return "";
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

    public List<Invite> getAll(String receiverId) {
        try {
            String selectQuery = "SELECT * FROM organisationinvites WHERE receiverid = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, receiverId);
            List<Invite> invites = new ArrayList<>();
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                invites.add(Invite.fromResultSet(resultSet));
            }
            return invites;
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

    public boolean delete(Invite invite) {
        try {
            String deleteQuery = "DELETE FROM organisationinvites WHERE senderid = ? AND receiverid = ? AND organisationid = ?";
            PreparedStatement deleteStatement = this.connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, invite.senderId());
            deleteStatement.setString(2, invite.receiverId());
            deleteStatement.setString(3, invite.organisationId());
            deleteStatement.executeUpdate();
            return deleteStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Invite for [" + invite.receiverId() +
                    "] from [" + invite.senderId() + "] to join [" + invite.organisationId() + "]");
        }
    }
}
