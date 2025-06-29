package ir.maktabsharif.home_service.exception;

public class ExpertHasAnActiveOrderException extends RuntimeException{
    public ExpertHasAnActiveOrderException() {
        super("Expert has an active order");
    }
}
