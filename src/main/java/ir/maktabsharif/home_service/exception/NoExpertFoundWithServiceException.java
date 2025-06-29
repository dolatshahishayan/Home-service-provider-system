package ir.maktabsharif.home_service.exception;

public class NoExpertFoundWithServiceException extends RuntimeException {
    public NoExpertFoundWithServiceException() {
        super("No expert found with service");
    }
}
