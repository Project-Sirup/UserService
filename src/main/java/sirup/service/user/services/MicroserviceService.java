package sirup.service.user.services;

import sirup.service.user.dto.Microservice;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MicroserviceService extends AbstractService<Microservice> {

    public String add(Microservice microservice) throws CouldNotMakeResourceException {
        throw new RuntimeException("Not implemented");
    }

    public Microservice get(String id) throws ResourceNotFoundException {
        throw new RuntimeException("Not implemented");
    }

    public Microservice getBy(String columnName, String key) throws ResourceNotFoundException {
        throw new RuntimeException("Not implemented");
    }

    public List<Microservice> getAll(String id) {
        throw new RuntimeException("Not implemented");
    }

    public boolean update(Microservice microservice) throws ResourceNotFoundException {
        throw new RuntimeException("Not implemented");
    }

    public boolean delete(String id) throws ResourceNotFoundException {
        try {
            String deleteQuery = "DELETE FROM microservices WHERE projectId = ?";
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
