package com.paymentology.transactionprocessor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidFileTypeException extends ResponseStatusException {

    public InvalidFileTypeException(String message){
        super(HttpStatus.BAD_REQUEST, message);
    }

}
