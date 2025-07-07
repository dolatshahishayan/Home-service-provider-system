package ir.maktabsharif.home_service.util;

import ir.maktabsharif.home_service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllOtherExceptions(Exception e) {
        return new ResponseEntity<>("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
