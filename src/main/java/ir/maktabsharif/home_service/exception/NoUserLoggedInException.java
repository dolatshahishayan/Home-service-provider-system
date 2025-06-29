package ir.maktabsharif.home_service.exception;

public class NoUserLoggedInException extends RuntimeException{
    public NoUserLoggedInException(String message) {
        super(message);
    }
}
