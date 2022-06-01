package com.paymentology.transactionprocessor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidFileException extends ResponseStatusException {
    public InvalidFileException(String message) {
        super(HttpStatus.BAD_REQUEST,message);
    }
}
