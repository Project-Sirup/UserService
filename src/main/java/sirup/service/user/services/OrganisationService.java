package sirup.service.user.services;

import sirup.service.user.dto.Organisation;
import sirup.service.user.dto.User;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static sirup.service.log.rpc.client.ColorUtil.*;

public class OrganisationService extends AbstractService<Organisation> {

    public String add(Organisation organisation) throws CouldNotMakeResourceException {
        try {
            String insertQuery = "INSERT INTO organisations (organisationid, organisationname) VALUES (?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, organisation.organisationId());
            insertStatement.setString(2, organisation.organisationName());
            insertStatement.execute();
            logger.info(name("Organisation"), id(organisation.getId()), action("created"));
            return organisation.getId();
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

    public Organisation get(String id) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT * FROM organisations WHERE organisationId = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            logger.info(name("Organisation"), id(id), action("found"));
            return Organisation.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find organisation with id: " + id);
        } catch (CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public List<Organisation> getAll(String id) throws ResourceNotFoundException {
        List<Organisation> organisations = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM organisations o " +
                    "FULL JOIN organisationpermissions op ON o.organisationid = op.organisationid " +
                    "WHERE op.userid = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    Organisation organisation = Organisation.fromResultSet(resultSet);
                    organisations.add(organisation);
                } catch (CouldNotMakeResourceException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find organisations for user with id: " + id);
        }
        logger.info(name("Organisations"), id(id), action("found all"));
        return organisations;
    }

    public List<User> getUsers(String organisationId) throws ResourceNotFoundException {
        throw new ResourceNotFoundException();

    }

    public boolean update(Organisation organisation, String userId) throws ResourceNotFoundException {
        try {
            String updateQuery = "UPDATE organisations o SET organisationName = ? " +
                    "WHERE EXISTS(" +
                    "SELECT * FROM organisationpermissions oP " +
                    "INNER JOIN users u ON u.userid = oP.userId " +
                    "WHERE oP.organisationid = o.organisationid " +
                    "AND oP.permissionid >= 3 " +
                    "AND u.userId = ?" +
                    "AND o.organisationid = ?);";
            PreparedStatement updateStatement = this.connection.prepareStatement(updateQuery);
            updateStatement.setString(1, organisation.organisationName());
            updateStatement.setString(2, userId);
            updateStatement.setString(2, organisation.organisationId());
            updateStatement.executeUpdate();
            logger.info(name("Organisation"), id(organisation.getId()), action("updated"));
            return updateStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public boolean delete(String id) throws ResourceNotFoundException {
        try {
            String deleteQuery = "DELETE FROM organisations WHERE organisationId = ?";
            PreparedStatement deleteStatement = this.connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, id);
            deleteStatement.executeUpdate();
            logger.info(name("Organisation"), id(id), action("deleted"));
            return deleteStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException(e.getMessage());
        }
    }
}
