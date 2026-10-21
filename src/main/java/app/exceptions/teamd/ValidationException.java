package app.exceptions.teamd;

    // Kastes når brugerens input ikke er gyldigt
    public class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }