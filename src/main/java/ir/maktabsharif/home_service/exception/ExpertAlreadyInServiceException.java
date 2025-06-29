package ir.maktabsharif.home_service.exception;

public class ExpertAlreadyInServiceException extends RuntimeException{
    public ExpertAlreadyInServiceException() {
        super("Expert Already In Service");
    }
}
