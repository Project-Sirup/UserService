package sirup.service.user.api;

import org.junit.Test;
import sirup.service.auth.rpc.client.AuthClient;
import sirup.service.user.database.PostgreSQL;
import sirup.service.user.dto.User;
import sirup.service.user.services.UserService;

import static org.junit.Assert.*;

public class ApiTest {

    @Test
    public void builderTest() {
        AuthClient.init("localhost",50051);
        Api api = Api.builder()
                .port(2149)
                .context(
                        Context.builder()
                                .database(new PostgreSQL())
                                .addService(User.class, new UserService()))
                .build();
        assert api != null;
    }

}