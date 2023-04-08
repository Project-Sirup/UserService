package sirup.service.user.services;

import sirup.service.user.dto.Project;
import sirup.service.user.exceptions.CouldNotMakeResourceException;
import sirup.service.user.exceptions.ResourceNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static sirup.service.log.rpc.client.ColorUtil.*;

public class ProjectService extends AbstractService<Project> {

    public String add(Project project) throws CouldNotMakeResourceException {
        try {
            String insertQuery = "INSERT INTO projects (projectid, projectname, organisationid) VALUES (?, ?, ?);";
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            insertStatement.setString(1, project.projectId());
            insertStatement.setString(2, project.projectName());
            insertStatement.setString(3, project.organisationId());
            insertStatement.execute();
            logger.info(name("Project"), id(project.getId()), action("created"));
            return project.getId();
        } catch (SQLException e) {
            throw new CouldNotMakeResourceException(e.getMessage());
        }
    }

    public Project get(String id) throws ResourceNotFoundException {
        try {
            String selectQuery = "SELECT * FROM projects WHERE projectId = ?;";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            logger.info(name("Project"), id(id), action("found"));
            return Project.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Could not find user with id: " + id);
        } catch (CouldNotMakeResourceException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public List<Project> getAll(String id) throws ResourceNotFoundException {
        List<Project> projects = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM projects p " +
                    "INNER JOIN projectpermissions pP ON pP.projectId = p.projectId " +
                    "INNER JOIN organisations o ON o.organisationId = p.organisationId " +
                    "WHERE o.organisationid = ?";
            PreparedStatement selectStatement = this.connection.prepareStatement(selectQuery);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    Project project = Project.fromResultSet(resultSet);
                    projects.add(project);
                } catch (CouldNotMakeResourceException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
        logger.info(name("Projects"), id(id), action("found all"));
        return projects;
    }

    public boolean update(Project project) throws ResourceNotFoundException {
        try {
            //TODO: Check permissions
            String updateQuery = "UPDATE projects SET projectName = ? WHERE projectId = ?";
            PreparedStatement updateStatement = this.connection.prepareStatement(updateQuery);
            updateStatement.setString(1, project.projectName());
            updateStatement.setString(2, project.projectId());
            updateStatement.executeUpdate();
            logger.info(name("Project"), id(project.getId()), action("updated"));
            return updateStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public boolean delete(String id) throws ResourceNotFoundException {
        try {
            String deleteQuery = "DELETE FROM projects WHERE projectId = ?";
            PreparedStatement deleteStatement = this.connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, id);
            deleteStatement.executeUpdate();
            logger.info(name("Project"), id(id), action("deleted"));
            return deleteStatement.getUpdateCount() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException(e.getMessage());
        }
    }
}
