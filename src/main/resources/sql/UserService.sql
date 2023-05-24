CREATE DATABASE sirupUser;


CREATE TABLE users (
    userId VARCHAR(255) UNIQUE NOT NULL,
    userName VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    systemAccess INTEGER NOT NULL,
    PRIMARY KEY(userId)
);

CREATE TABLE organisations (
    organisationId VARCHAR(255) NOT NULL,
    organisationName VARCHAR(255) NOT NULL,
    PRIMARY KEY(organisationId)
    );

CREATE TABLE projects (
    projectId VARCHAR(255) NOT NULL,
    projectName VARCHAR(255) NOT NULL,
    organisationId VARCHAR(255) NOT NULL REFERENCES organisations(organisationId) ON DELETE CASCADE,
    PRIMARY KEY(projectId)
);

CREATE TABLE microservices (
    microserviceId VARCHAR(255) NOT NULL,
    microserviceName VARCHAR(255) NOT NULL,
    microserviceFile JSON,
    projectId VARCHAR(255) NOT NULL REFERENCES projects(projectId) ON DELETE CASCADE,
    PRIMARY KEY(microserviceId)
);

CREATE TABLE permissions (
    permissionId INTEGER NOT NULL,
    permissionName VARCHAR(255) NOT NULL,
    PRIMARY KEY(permissionId)
);

CREATE TABLE organisationPermissions (
    userId VARCHAR(255) NOT NULL REFERENCES users(userId) ON DELETE CASCADE,
    organisationId VARCHAR(255) NOT NULL REFERENCES organisations(organisationId) ON DELETE CASCADE,
    permissionId INTEGER NOT NULL REFERENCES permissions(permissionId),
    PRIMARY KEY(userId, organisationId, permissionId)
);

CREATE TABLE projectPermissions (
    userId VARCHAR(255) NOT NULL REFERENCES users(userId) ON DELETE CASCADE,
    projectId VARCHAR(255) NOT NULL REFERENCES projects(projectId) ON DELETE CASCADE,
    permissionId INTEGER NOT NULL REFERENCES permissions(permissionId),
    PRIMARY KEY(userId, projectId, permissionId)
);

CREATE TABLE microservicePermissions (
    userId VARCHAR(255) NOT NULL REFERENCES users(userId) ON DELETE CASCADE,
    microserviceId VARCHAR(255) NOT NULL REFERENCES microservices(microserviceId) ON DELETE CASCADE,
    permissionId INTEGER NOT NULL REFERENCES permissions(permissionId),
    PRIMARY KEY(userId, microserviceId, permissionId)
);

INSERT INTO permissions (permissionId, permissionName) VALUES
    (-1,'NO_ACCESS'),
    (0,'VIEW'),
    (1,'EDIT'),
    (2,'MANAGER'),
    (3,'ADMIN'),
    (4,'OWNER');

CREATE TABLE organisationInvites (
    senderId VARCHAR(255) NOT NULL REFERENCES users(userId) ON DELETE CASCADE,
    receiverId VARCHAR(255) NOT NULL REFERENCES users(userId) ON DELETE CASCADE,
    organisationId VARCHAR(255) NOT NULL REFERENCES organisations(organisationId) ON DELETE CASCADE,
    PRIMARY KEY (senderId, receiverId, organisationId)
);
