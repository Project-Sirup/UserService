package sirup.service.user.services;

import sirup.service.user.dto.Microservice;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static sirup.service.log.rpc.client.ColorUtil.*;

public class MicroserviceService extends AbstractService<Microservice> {

    public String add(Microservice microservice) throws CouldNotMakeResourceException {
        try {
            String insertQuery = "INSERT INTO microservices (microserviceid, microservicename, projectid) VALUES (?,?,?)";
            PreparedStatement insetStatement = this.connection.prepareStatement(insertQuery);
            insetStatement.setString(1, microservice.microserviceId());
            insetStatement.setString(2, microservice.microserviceName());
            insetStatement.setString(3, microservice.projectId());
            insetStatement.execute();
            logger.info(name("Microservice"), id(microservice.getId()), action("created"));
            return microservice.getId();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

    public Microservice get(String id) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT * FROM microservices WHERE microserviceid = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            logger.info(name("Microservice"), id(id), action("found"));
            return Microservice.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find Microservice with id: " + id);
        } catch (CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public List<Microservice> getAll(String id) throws ResourceNotFoundException {
        List<Microservice> microservices = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM microservices m " +
                    "INNER JOIN microservicePermissions mP ON mP.microserviceId = m.microserviceid " +
                    "INNER JOIN projects p ON m.projectId = p.projectId " +
                    "WHERE p.projectId = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    Microservice microservice = Microservice.fromResultSet(resultSet);
                    microservices.add(microservice);
                } catch (CouldNotMakeResourceException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
        logger.info(name("Microservices"), id(id), action("found all"));
        return microservices;
    }

    public boolean update(Microservice microservice) throws ResourceNotFoundException {
        try {
            //TODO: Check permissions
            String updateQuery = "UPDATE microservices SET microservicename = ? WHERE microserviceid = ?";
            PreparedStatement updateStatement = this.connection.prepareStatement(updateQuery);
            updateStatement.setString(1, microservice.microserviceName());
            updateStatement.setString(2, microservice.getId());
            updateStatement.executeUpdate();
            logger.info(name("Microservice"), id(microservice.getId()), action("updated"));
            return updateStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public boolean delete(String id) throws ResourceNotFoundException {
        try {
            String deleteQuery = "DELETE FROM microservices WHERE projectId = ?";
            PreparedStatement deleteStatement = this.connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, id);
            deleteStatement.executeUpdate();
            logger.info(name("Microservice"), id(id), action("deleted"));
            return deleteStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException(e.getMessage());
        }
    }
}
