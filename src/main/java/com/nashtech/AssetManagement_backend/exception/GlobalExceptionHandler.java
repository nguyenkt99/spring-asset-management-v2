package com.nashtech.AssetManagement_backend.exception;


import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Map<String, Object> handleValidationExceptions(
//            MethodArgumentNotValidException ex) {
//
//        Map<String, Object> map = new HashMap<>();
//
//        Map<String, String> errors = new HashMap<>();
//
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//
//        map.put("message", errors);
//        return map;
//    }
//
//
//
//
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    @ResponseStatus(value = HttpStatus.NOT_FOUND)
//    public Map<String, Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
//        Map<String, Object> map = new HashMap<>();
//
//
//        map.put("message", ex.getMessage());
//        return map;
//    }
//
//
//    @ExceptionHandler(BadRequestException.class)
//    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
//    public Map<String, Object> handleBadRequestException(BadRequestException ex) {
//        Map<String, Object> map = new HashMap<>();
//
//        map.put("message", ex.getMessage());
//        return map;
//    }
//





}
