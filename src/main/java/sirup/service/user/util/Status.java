package sirup.service.user.util;

public enum Status {
    OK(200,"Request handled"),

    BAD_REQUEST(400,"Bad request"),
    UNAUTHORIZED(401,"Wrong login credentials"),
    DOES_NOT_EXIST(417,"Item does not exist"),
    ALREADY_EXISTS(417,"Item already exists"),
    INVALID_TOKEN(498,"Invalid token"),

    NOT_IMPLEMENTED(501,"Endpoint not yet implemented");
    private final int code;
    private final String message;
    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return this.code;
    }
    public String getMessage() {
        return this.message;
    }
}
