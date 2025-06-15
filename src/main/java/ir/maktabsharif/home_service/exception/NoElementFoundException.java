package ir.maktabsharif.home_service.exception;

public class NoElementFoundException extends RuntimeException{
    public NoElementFoundException(){
        super("No element found");
    }
}
