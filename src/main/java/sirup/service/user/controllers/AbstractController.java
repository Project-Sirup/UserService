package sirup.service.user.controllers;

import sirup.service.user.database.IDatabase;

public abstract class AbstractController {
    protected final IDatabase database;

    public AbstractController(final IDatabase database) {
        this.database = database;
    }
}
