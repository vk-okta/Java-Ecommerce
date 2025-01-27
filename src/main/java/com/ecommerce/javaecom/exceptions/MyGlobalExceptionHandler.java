package com.ecommerce.javaecom.exceptions;

import com.ecommerce.javaecom.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// This Annotation tells spring that we are handling Global Exceptions on our own
@RestControllerAdvice
public class MyGlobalExceptionHandler {

    // Handles MethodArgumentNotValidException error.
    // Whenever this exception is thrown Spring will intercept and execute the following method
    // we get such errors for example when passing empty Category Name, this exception will be thrown.
    // Over-riding the default exception with custom login
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, String> response = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();

            // If custom message is defined in the Model, it will use that message
            String errorMessage = error.getDefaultMessage();
            response.put(fieldName, errorMessage);
        });

        return new ResponseEntity<Map<String, String>>(response, HttpStatus.BAD_REQUEST);
    }

    // A custom Exception Handler
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException exception) {
        String errorMessage = exception.getMessage();
        APIResponse response = new APIResponse(errorMessage, false);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIExceptions.class)
    public ResponseEntity<APIResponse> myAPIException(APIExceptions exception) {
        String errorMessage = exception.getMessage();
        APIResponse response = new APIResponse(errorMessage, false);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
