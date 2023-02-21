package sirup.service.user.api.rpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import sirup.service.auth.rpc.proto.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SirupAuthClient {

    private final ManagedChannel managedChannel;
    private final SirupAuthGrpc.SirupAuthBlockingStub authService;

    public SirupAuthClient() {
        managedChannel = ManagedChannelBuilder.forAddress("localhost",50051).usePlaintext().build();
        authService = SirupAuthGrpc.newBlockingStub(managedChannel);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }
    public boolean auth(String token) {
        AuthRequest req = AuthRequest.newBuilder().setToken(token).build();
        AuthResponse res;
        try {
            res = authService.auth(req);
        } catch (StatusRuntimeException sre) {
            sre.printStackTrace();
            return false;
        }
        return res.getTokenValid();
    }

    public Optional<String> token(String username, String password) {
        TokenRequest req = TokenRequest.newBuilder().setCredentials(CredentialsRpc.newBuilder().setUsername(username).setPassword(password)).build();
        TokenResponse res;
        try {
            res = authService.token(req);
        } catch (StatusRuntimeException sre) {
            sre.printStackTrace();
            return Optional.empty();
        }
        return Optional.of(res.getToken());
    }

}
