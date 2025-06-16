package ir.maktabsharif.home_service.exception;

public class UserWithSameEmailExistsException extends RuntimeException {
    public UserWithSameEmailExistsException() {
        super("User With Same Email Exists");
    }
}
