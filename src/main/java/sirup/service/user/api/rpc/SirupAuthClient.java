package sirup.service.user.api.rpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import sirup.service.auth.rpc.proto.*;
import sirup.service.user.util.Result;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SirupAuthClient {

    private final ManagedChannel managedChannel;
    private final SirupAuthGrpc.SirupAuthBlockingStub authService;
    private static SirupAuthClient instance;

    private SirupAuthClient() {
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

    public static SirupAuthClient getInstance() {
        return instance == null ? instance = new SirupAuthClient() : instance;
    }

    public Result<String, Boolean> auth(String token) {
        AuthRequest req = AuthRequest.newBuilder().setToken(token).build();
        AuthResponse res;
        try {
            res = authService.auth(req);
        } catch (StatusRuntimeException e) {
            System.err.println(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success(res.getTokenValid());
    }

    public Result<String, String> token(String userID) {
        TokenRequest req = TokenRequest.newBuilder().setCredentials(CredentialsRpc.newBuilder().setUserID(userID)).build();
        TokenResponse res;
        try {
            res = authService.token(req);
        } catch (StatusRuntimeException e) {
            System.err.println(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success(res.getToken());
    }

}
