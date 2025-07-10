package ir.maktabsharif.home_service.util;

import ir.maktabsharif.home_service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CouldNotUpdateException.class)
    public ResponseEntity<String> handleCouldNotUpdateException(CouldNotUpdateException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateInfoException.class)
    public ResponseEntity<String> handleDuplicateInfoException(DuplicateInfoException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ExpertAlreadyInServiceException.class)
    public ResponseEntity<String> handleExpertAlreadyInServiceException(ExpertAlreadyInServiceException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoElementFoundException.class)
    public ResponseEntity<String> handleNoElementFoundException(NoElementFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExpertHasAnActiveOrderException.class)
    public ResponseEntity<String> handleExpertHasAnActiveOrderException(ExpertHasAnActiveOrderException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImageFormatException.class)
    public ResponseEntity<String> handleImageFormatException(ImageFormatException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImageLengthOutOfBoundException.class)
    public ResponseEntity<String> handleImageLengthOutOfBoundException(ImageLengthOutOfBoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleInvalidRequestException(InvalidRequestException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoExpertFoundWithServiceException.class)
    public ResponseEntity<String> handleNoExpertFoundWithServiceException(NoExpertFoundWithServiceException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoUserFoundWithGivenCredentialsException.class)
    public ResponseEntity<String> handleNoUserFoundWithGivenCredentialsException(NoUserFoundWithGivenCredentialsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserWithSameEmailExistsException.class)
    public ResponseEntity<String> handleUserWithSameEmailExistsException(UserWithSameEmailExistsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<String> handleInsufficientFundsException(InsufficientFundsException e) {
        return new ResponseEntity<>(e.getMessage()+"\n http://localhost:8080/login.html\nYou can pay for your orders with given link.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllOtherExceptions(Exception e) {
        return new ResponseEntity<>("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
