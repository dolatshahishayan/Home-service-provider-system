package ir.maktabsharif.home_service.exception;

public class NoImageFoundException extends RuntimeException{
    public NoImageFoundException(){
        super("No image found");
    }
}
