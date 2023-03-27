package sirup.service.user.services;

import sirup.service.user.dto.Organisation;
import sirup.service.user.dto.PrivilegeLevel;
import sirup.service.user.dto.User;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;
import sirup.service.user.interfaces.ServiceRelation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrganisationService extends AbstractService<Organisation> {

    public final UserPermission userPermission;
    public final UserInvite userInvite;
    public OrganisationService(){
        this.userPermission = new UserPermission(this.connection);
        this.userInvite = new UserInvite(this.connection);
    }

    @Override
    public boolean add(Organisation organisation) {
        try {
            logger.info(this.getClass().getSimpleName() + " -> add -> " + organisation.getId());
            String insertQuery = "INSERT INTO organisations (organisationid, organisationname) VALUES (?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, organisation.organisationId().toString());
            insertStatement.setString(2, organisation.organisationName());
            return insertStatement.execute();
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

    @Override
    public Organisation get(String id) throws ResourceNotFoundException {
        try {
            logger.info(this.getClass().getSimpleName() + " -> get -> " + id);
            String selectQuery = "SELECT * FROM organisations WHERE organisationId = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            return Organisation.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find organisation with id: " + id);
        } catch (CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public Organisation getBy(String columnName, String key) throws ResourceNotFoundException {
        try {
            logger.info(this.getClass().getSimpleName() + " -> getBy -> " + columnName + " = " + key);
            String selectQuery = "SELECT * FROM organisations WHERE " + columnName + " = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, key);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            return Organisation.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find user: " + key);
        } catch (CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public boolean update(Organisation organisation) throws ResourceNotFoundException {
        try {
            logger.info(this.getClass().getSimpleName() + " -> update -> " + organisation.getId());
            String updateQuery = "UPDATE organisations SET organisationName = ? WHERE organisationId = ?";
            PreparedStatement updateStatement = this.connection.prepareStatement(updateQuery);
            updateStatement.setString(1, organisation.organisationName());
            updateStatement.setString(2, organisation.organisationId().toString());
            return updateStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public boolean delete(String id) throws ResourceNotFoundException {
        try {
            logger.info(this.getClass().getSimpleName() + " -> delete -> " + id);
            String deleteQuery = "DELETE FROM organisations WHERE organisationId = ?";
            PreparedStatement deleteStatement = this.connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, id);
            deleteStatement.executeUpdate();
            return deleteStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public static class UserPermission implements ServiceRelation<Organisation, User, PrivilegeLevel> {

        private final Connection connection;
        private UserPermission(final Connection connection) {
            this.connection = connection;
        }

        @Override
        public boolean add(Organisation organisation, User user, PrivilegeLevel privilegeLevel) {
            try {
                String insertQuery = "INSERT INTO organisationpermissions (userid, organisationid, permissionid) VALUES (?, ?, ?);";
                PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
                insertStatement.setString(1, user.userId().toString());
                insertStatement.setString(2, organisation.organisationId().toString());
                insertStatement.setInt(3, privilegeLevel.id);
                return insertStatement.execute();
            } catch (SQLException e) {
                throw new CouldNotMakeResourceException(e.getMessage());
            }
        }

        @Override
        public List<PrivilegeLevel> getAll(User user) {
            return null;
        }

        @Override
        public PrivilegeLevel get(Organisation organisation, User user) {
            return null;
        }

        @Override
        public boolean update(Organisation organisation, User user, PrivilegeLevel privilegeLevel) {
            return false;
        }

        @Override
        public boolean delete(Organisation organisation, User user, PrivilegeLevel privilegeLevel) {
            return false;
        }
    }
    public static class UserInvite implements ServiceRelation<User, User, Organisation> {

        private final Connection connection;
        private UserInvite(final Connection connection) {
            this.connection = connection;
        }

        @Override
        public boolean add(User sender, User receiver, Organisation organisation) {
            return false;
        }

        @Override
        public List<Organisation> getAll(User user) {
            return null;
        }

        @Override
        public Organisation get(User sender, User receiver) {
            return null;
        }

        @Override
        public boolean update(User sender, User receiver, Organisation organisation) {
            return false;
        }

        @Override
        public boolean delete(User sender, User receiver, Organisation organisation) {
            return false;
        }
    }
}
