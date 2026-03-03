package service;

/**
 * Exception thrown when business validation fails.
 * Used by service layer to indicate invalid data or business rule violations.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
