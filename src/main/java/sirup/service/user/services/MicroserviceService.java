package sirup.service.user.services;

import sirup.service.user.dto.Microservice;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MicroserviceService extends AbstractService<Microservice> {

    @Override
    public boolean add(Microservice microservice) throws CouldNotMakeResourceException {
        return false;
    }

    @Override
    public Microservice get(String id) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public Microservice getBy(String columnName, String key) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public boolean update(Microservice microservice) throws ResourceNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String id) throws ResourceNotFoundException {
        try {
            String deleteQuery = "DELETE FROM services WHERE projectId = ?";
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
