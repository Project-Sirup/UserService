package sirup.service.user.exceptions;

public class CouldNotMakeResourceException extends RuntimeException {
    public CouldNotMakeResourceException() {
        super();
    }
    public CouldNotMakeResourceException(String message) {
        super(message);
    }
}
