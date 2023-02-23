DROP TABLE users, services, projects, organisations, servicePermissions, projectPermissions, organisationPermissions, permissions CASCADE ;

CREATE TABLE users (
    userID VARCHAR(255) UNIQUE NOT NULL,
    userName VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY(userID)
);

CREATE TABLE services (
    serviceID VARCHAR(255) NOT NULL,
    serviceName VARCHAR(255) NOT NULL,
    serviceFile JSON,
    projectID VARCHAR(255) NOT NULL,
    PRIMARY KEY(serviceID)
);

CREATE TABLE projects (
    projectID VARCHAR(255) NOT NULL,
    projectName VARCHAR(255) NOT NULL,
    organisationID VARCHAR(255) NOT NULL,
    PRIMARY KEY(projectID)
);

CREATE TABLE organisations (
    organisationID VARCHAR(255) NOT NULL,
    organisationName VARCHAR(255) NOT NULL,
    PRIMARY KEY(organisationID)
);

CREATE TABLE permissions (
    permissionID INTEGER NOT NULL,
    permissionName VARCHAR(255) NOT NULL,
    PRIMARY KEY(permissionID)
);

CREATE TABLE organisationPermissions (
    userID VARCHAR(255) NOT NULL REFERENCES users(userID) ON DELETE CASCADE,
    organisationID VARCHAR(255) NOT NULL REFERENCES organisations(organisationID) ON DELETE CASCADE,
    permissionID INTEGER NOT NULL REFERENCES permissions(permissionID),
    PRIMARY KEY(userID, organisationID, permissionID)
);

CREATE TABLE projectPermissions (
    userID VARCHAR(255) NOT NULL REFERENCES users(userID) ON DELETE CASCADE,
    projectID VARCHAR(255) NOT NULL REFERENCES projects(projectID) ON DELETE CASCADE,
    permissionID INTEGER NOT NULL REFERENCES permissions(permissionID),
    PRIMARY KEY(userID, projectID, permissionID)
);

CREATE TABLE servicePermissions (
    userID VARCHAR(255) NOT NULL REFERENCES users(userID) ON DELETE CASCADE,
    serviceID VARCHAR(255) NOT NULL REFERENCES services(serviceID) ON DELETE CASCADE,
    permissionID INTEGER NOT NULL REFERENCES permissions(permissionID),
    PRIMARY KEY(userID, serviceID, permissionID)
);


INSERT INTO permissions (permissionID, permissionName) VALUES
    (0,'ADMIN'),
    (1,'MANAGER'),
    (2,'DEFAULT');