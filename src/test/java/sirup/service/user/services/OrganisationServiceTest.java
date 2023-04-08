package sirup.service.user.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sirup.service.user.api.Context;
import sirup.service.user.database.PostgreSQL;
import sirup.service.user.dto.Organisation;
import sirup.service.user.dto.User;

import java.util.UUID;

public class OrganisationServiceTest {

    OrganisationService service;
    PostgreSQL postgreSQL;
    Organisation testOrg;
    String uuid = UUID.randomUUID().toString();

    @Before
    public void setUp() throws Exception {
        postgreSQL = new PostgreSQL();
        postgreSQL.connect();
        Context context = Context.builder().database(postgreSQL).addService(Organisation.class, new OrganisationService()).build();
        service = (OrganisationService) context.getService(Organisation.class);
        service.init();
        testOrg = new Organisation(uuid,"TESTORG");
    }

    @After
    public void tearDown() throws Exception {
        postgreSQL.disconnect();
    }
}