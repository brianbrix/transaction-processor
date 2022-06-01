package com.paymentology.transactionprocessor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemsEmptyException extends ResponseStatusException {

    public ItemsEmptyException(String message){
        super(HttpStatus.BAD_REQUEST, message);
    }

}