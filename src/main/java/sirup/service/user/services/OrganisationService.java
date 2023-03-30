package sirup.service.user.services;

import sirup.service.user.dto.Microservice;
import sirup.service.user.dto.Organisation;
import sirup.service.user.dto.Project;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static sirup.service.log.rpc.client.ColorUtil.*;

public class OrganisationService extends AbstractService<Organisation> {

    public String add(Organisation organisation) {
        try {
            logger.info(this.getClass().getSimpleName() + " -> " + id(organisation.getId()) + " -> " + action("created"));
            String insertQuery = "INSERT INTO organisations (organisationid, organisationname) VALUES (?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, organisation.organisationId());
            insertStatement.setString(2, organisation.organisationName());
            insertStatement.execute();
            return organisation.getId();
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

    public Organisation get(String id) throws ResourceNotFoundException {
        try {
            logger.info(this.getClass().getSimpleName() + " -> " + id(id) + " -> " + action("found"));
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

    public Organisation getBy(String columnName, String key) throws ResourceNotFoundException {
        try {
            logger.info(this.getClass().getSimpleName() + " -> " + columnName + " = " + key + " -> " + action("found"));
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

    public List<Organisation> getAll(String id) {
        List<Organisation> organisations = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM organisations o " +
                    "INNER JOIN organisationpermissions op ON o.organisationid = op.organisationid " +
                    "FULL JOIN projects p ON oP.organisationid = p.organisationid " +
                    "FULL JOIN microservices m on p.projectid = m.projectid " +
                    "INNER JOIN users u ON oP.userId = u.userId " +
                    "WHERE op.userid = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                Organisation organisation = Organisation.fromResultSet(resultSet);
                Project project = null;
                try {
                    project = Project.fromResultSet(resultSet);
                } catch (CouldNotMakeResourceException e) {}
                Microservice microservice = null;
                try {
                    microservice = Microservice.fromResultSet(resultSet);
                } catch (CouldNotMakeResourceException e) {}
                if (project != null) {
                    System.out.println(project.projectName());
                    if (microservice != null) {
                        project.microservices().add(microservice);
                    }
                    organisation.projects().add(project);
                }
                organisations.add(organisation);
            }
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find organisations for user with id: " + id);
        } catch (CouldNotMakeResourceException e) {
            System.err.println(e.getMessage());
        }
        return organisations;
    }

    public boolean update(Organisation organisation) throws ResourceNotFoundException {
        try {
            logger.info(this.getClass().getSimpleName() + " -> " + id(organisation.getId()) + " -> " + action("updated"));
            String updateQuery = "UPDATE organisations SET organisationName = ? WHERE organisationId = ?";
            PreparedStatement updateStatement = this.connection.prepareStatement(updateQuery);
            updateStatement.setString(1, organisation.organisationName());
            updateStatement.setString(2, organisation.organisationId());
            updateStatement.executeUpdate();
            return updateStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public boolean delete(String id) throws ResourceNotFoundException {
        try {
            logger.info(this.getClass().getSimpleName() + " -> " + id(id) + " -> " + action("deleted"));
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
}
