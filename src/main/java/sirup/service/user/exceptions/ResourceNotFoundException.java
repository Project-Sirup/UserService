package sirup.service.user.exceptions;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException() {
        super();
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(Throwable throwable) {
        super(throwable);
    }
    public ResourceNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
