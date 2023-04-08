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

    public String add(Invite invite) throws CouldNotMakeResourceException {
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
            throw new CouldNotMakeResourceException("Could not create invitation");
        }
    }

    public Invite get(String receiverId, String senderId, String organisationId) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT sender.userid, sender.username, receiver.userid, receiver.username, o.organisationid, o.organisationname FROM organisationInvites oi " +
                    "INNER JOIN users sender ON oi.senderid = sender.userId " +
                    "INNER JOIN users receiver ON oi.receiverid = receiver.userid " +
                    "INNER JOIN organisations o ON oi.organisationid = o.organisationid " +
                    "WHERE sender.userid = ? " +
                    "AND receiver.userId = ? " +
                    "AND o.organisationId = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, senderId);
            selectStatement.setString(2, receiverId);
            selectStatement.setString(3, organisationId);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            return Invite.fromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find invitation");
        } catch (CouldNotMakeResourceException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public List<Invite> getAllReceived(String receiverId) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT sender.userid, sender.username, receiver.userid, receiver.username, o.organisationid, o.organisationname FROM organisationInvites oi " +
                    "INNER JOIN users sender ON oi.senderid = sender.userId " +
                    "INNER JOIN users receiver ON oi.receiverid = receiver.userid " +
                    "INNER JOIN organisations o ON oi.organisationid = o.organisationid " +
                    "WHERE receiver.userid = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, receiverId);
            ResultSet resultSet = selectStatement.executeQuery();
            return _getAll(resultSet);
        } catch (SQLException | CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException("Could not find any invitation with receiverId [" + receiverId + "] ");
        }
    }

    public List<Invite> getAllSent(String senderId) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT * FROM organisationinvites WHERE senderid = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, senderId);
            ResultSet resultSet = selectStatement.executeQuery();
            return _getAll(resultSet);
        } catch (SQLException | CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException("Could not find any invitations with senderId [" + senderId + "]");
        }
    }

    private List<Invite> _getAll(ResultSet resultSet) throws SQLException, CouldNotMakeResourceException {
        List<Invite> invites = new ArrayList<>();
        while (resultSet.next()) {
            invites.add(Invite.fromResultSet(resultSet));
        }
        return invites;
    }

    public boolean delete(Invite invite) throws ResourceNotFoundException {
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
