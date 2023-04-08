package sirup.service.user.exceptions;

public class CouldNotMakeResourceException extends Exception {
    public CouldNotMakeResourceException() {
        super();
    }
    public CouldNotMakeResourceException(String message) {
        super(message);
    }
    public CouldNotMakeResourceException(Throwable throwable) {
        super(throwable);
    }
    public CouldNotMakeResourceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
