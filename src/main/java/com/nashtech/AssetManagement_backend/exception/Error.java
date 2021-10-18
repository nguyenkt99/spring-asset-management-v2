package com.nashtech.AssetManagement_backend.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
public class Error {
    protected  int status;
    protected  String error;
    protected  String message;
    @JsonIgnore
    protected HttpStatus httpStatus;
    public ResponseEntity<Object> generateResponseEntity(){
        return new ResponseEntity<Object>(this, this.httpStatus);
    }

}
