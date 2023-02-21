package sirup.service.user;

import sirup.service.user.api.Api;
import sirup.service.user.database.PostgreSQL;

public class Main {
    public static void main(String[] args) {
        Api.builder()
                .port(2149)
                .database(new PostgreSQL())
                .build()
                .start();
    }
}
