package sirup.service.user.exceptions;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException() {
        super();
    }
    public ExternalServiceException(String message) {
        super(message);
    }
}
