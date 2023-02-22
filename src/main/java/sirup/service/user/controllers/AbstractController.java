package sirup.service.user.controllers;

import sirup.service.user.database.IDatabase;

import java.sql.Connection;

public abstract class AbstractController {
    protected final Connection connection;

    public AbstractController(final Connection connection) {
        this.connection = connection;
    }
}
