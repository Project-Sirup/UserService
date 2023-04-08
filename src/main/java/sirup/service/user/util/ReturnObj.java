package sirup.service.user.util;

public record ReturnObj<T>(int statusCode, String message, T data) {
    public ReturnObj(String message) {
        this(Status.OK.getCode(), message, null);
    }
    public ReturnObj(Status status) {
        this(status.getCode(), status.getMessage(), null);
    }
    public ReturnObj(Status status, String message) {
        this(status.getCode(), message, null);
    }
    public ReturnObj(String message, T t) {
        this(Status.OK.getCode(), message, t);
    }
    public ReturnObj(Status status, T t) {
        this(status.getCode(), status.getMessage(), t);
    }
    public ReturnObj(Status status, String message, T t) {
        this(status.getCode(), message, t);
    }
}