package team07.Banking_System.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> illegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError error = new StandardError(System.currentTimeMillis(), status.value(), e.getMessage(), "Bad Request", request.getRequestURI());

        return ResponseEntity.status(status).body(error);

    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<StandardError> illegalStateException(IllegalStateException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        StandardError error =  new StandardError(System.currentTimeMillis(), status.value(), e.getMessage(), "Conflicting request", request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<StandardError> NoSuchElementException(NoSuchElementException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        StandardError error = new StandardError(System.currentTimeMillis(), status.value(), e.getMessage(), "Not Found", request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }



}
