package ir.maktabsharif.home_service.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("Insufficient funds. Please charge your wallet");
    }
}
