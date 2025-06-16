package ir.maktabsharif.home_service.exception;

public class NoUserFoundWithGivenCredentialsException extends RuntimeException {
    public NoUserFoundWithGivenCredentialsException() {
        super("No user found with given credentials");
    }
}
