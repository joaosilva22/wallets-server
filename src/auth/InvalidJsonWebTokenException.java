package auth;

public class InvalidJsonWebTokenException extends Exception {
    public InvalidJsonWebTokenException(String message) {
        super(message);
    }
}
