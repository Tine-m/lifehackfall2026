package app.exceptions.teamd;

// Thrown when the user's input is not valid
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}