package sirup.service.user.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sirup.service.user.api.Context;
import sirup.service.user.database.PostgreSQL;
import sirup.service.user.dto.User;

public class UserServiceTest {

    UserService service;
    PostgreSQL postgreSQL;
    User testUser;

    @Before
    public void setUp() throws Exception {
        postgreSQL = new PostgreSQL();
        postgreSQL.connect();
        Context context = Context.builder().database(postgreSQL).addService(User.class, new UserService()).build();
        service = (UserService) context.getService(User.class);
        service.init();
        testUser = new User("TESTUSER","TESTPASS");
    }

    @After
    public void tearDown() throws Exception {
        postgreSQL.disconnect();
    }

    @Test
    public void add() {
        service.add(testUser);
    }

    @Test
    public void get() {
        User user = service.get("b380ccf9-7919-4078-b0d5-a590120db9dd");
        assert user != null;
    }

    @Test
    public void getBy() {
        service.getBy("userName", testUser.userName());
    }

    @Test
    public void update() {
        service.update(new User(testUser.userId(), "USERTEST"));
    }

    @Test
    public void delete() {
        service.delete(testUser.getId());
    }
}