package sirup.service.user.services;

import sirup.service.user.dto.Project;
import sirup.service.user.dto.User;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectService extends AbstractService<Project> {

    @Override
    public boolean add(Project project) throws CouldNotMakeResourceException {
        try {
            String insertQuery = "INSERT INTO projects (projectid, projectname, organisationid) VALUES (?, ?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, project.projectId().toString());
            insertStatement.setString(2, project.projectName());
            insertStatement.setString(3, project.organisationId().toString());
            return insertStatement.execute();
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

    @Override
    public Project get(String id) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT * FROM projects WHERE projectId = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            return Project.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find user with id: " + id);
        } catch (CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public Project getBy(String columnName, String key) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT * FROM projects WHERE ? = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, columnName);
            selectStatement.setString(2, key);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            return Project.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find user: " + key);
        } catch (CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public boolean update(Project project) throws ResourceNotFoundException {
        try {
            String updateQuery = "UPDATE projects SET projectName = ? WHERE projectId = ?";
            PreparedStatement updateStatement = this.connection.prepareStatement(updateQuery);
            updateStatement.setString(1, project.projectName());
            updateStatement.setString(2, project.projectId().toString());
            return updateStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public boolean delete(String id) throws ResourceNotFoundException {
        try {
            String deleteQuery = "DELETE FROM projects WHERE projectId = ?";
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
