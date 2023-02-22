CREATE TABLE user (
    userID VARCHAR(255),
    userName VARCHAR(255),
    password VARCHAR(255)
);

CREATE TABLE service (
    serviceID VARCHAR(255),
    serviceName VARCHAR(255)
);

CREATE TABLE project (
  projectID VARCHAR(255),
  projectName VARCHAR(255)
);

CREATE TABLE organisation (
    organisationID VARCHAR(255),
    organisationName VARCHAR(255)
);

CREATE TABLE permission (
  permissionID INT,
  permissionName VARCHAR(255)
);