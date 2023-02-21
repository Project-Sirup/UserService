package sirup.service.user.api.rpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import sirup.service.auth.rpc.proto.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SirupAuthClient {

    private final ManagedChannel managedChannel;
    private final SirupAuthGrpc.SirupAuthBlockingStub blockingStub;

    public SirupAuthClient() {
        managedChannel = ManagedChannelBuilder.forAddress("localhost",50051).usePlaintext().build();
        blockingStub = SirupAuthGrpc.newBlockingStub(managedChannel);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    private String token = "";

    public boolean auth() {
        AuthRequest req = AuthRequest.newBuilder().setToken(token).build();
        AuthResponse res;
        try {
            res = blockingStub.auth(req);
        } catch (StatusRuntimeException sre) {
            sre.printStackTrace();
            return false;
        }
        return res.getTokenValid();
    }

    public Optional<String> token() {
        TokenRequest req = TokenRequest.newBuilder().setCredentials(CredentialsRpc.newBuilder().setPassword("pass").setUsername("uname")).build();
        TokenResponse res;
        try {
            res = blockingStub.token(req);
        } catch (StatusRuntimeException sre) {
            sre.printStackTrace();
            return Optional.empty();
        }
        token = res.getToken();
        return Optional.of(token);
    }

}
