package app.exceptions.teame;

// Kastes når brugerens input ikke er gyldigt. Beskeden vises direkte til brugeren forhåneligt lol
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
