package com.nashtech.AssetManagement_backend.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BadRequestException.class)
    protected ResponseEntity<Object> BadRequestException(BadRequestException e) {
        return new Error(400, "Bad Request", e.getMessage(), HttpStatus.BAD_REQUEST).generateResponseEntity();
    }

    @ExceptionHandler(value = ConflictException.class)
    protected ResponseEntity<Object> ConflictException(ConflictException e) {
        return new Error(409, "Conflict", e.getMessage(), HttpStatus.CONFLICT).generateResponseEntity();
    }

    @ExceptionHandler(value = InvalidInputException.class)
    protected ResponseEntity<Object> InvalidInputException(InvalidInputException e) {
        return new Error(400, "Invalid Input Exception", e.getMessage(), HttpStatus.BAD_REQUEST).generateResponseEntity();
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    protected ResponseEntity<Object> ResourceNotFoundException(ResourceNotFoundException e) {
        return new Error(404, "Not Found", e.getMessage(), HttpStatus.NOT_FOUND).generateResponseEntity();
    }
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new Error(400, "Bad Request", "Enum invalid", HttpStatus.BAD_REQUEST).generateResponseEntity();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> MethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = "";
        for (ObjectError err : e.getBindingResult().getAllErrors()) {
            errorMessage+= ((FieldError)err).getField() +": "+err.getDefaultMessage()+"; ";
        }
        return new Error(400, "Not Valid Exception", errorMessage, HttpStatus.BAD_REQUEST).generateResponseEntity();

    }


}
