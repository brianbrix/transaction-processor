package com.paymentology.transactionprocessor.controller;

import com.paymentology.transactionprocessor.exceptions.InvalidFileException;
import com.paymentology.transactionprocessor.exceptions.InvalidFileTypeException;
import com.paymentology.transactionprocessor.exceptions.ItemsEmptyException;
import com.paymentology.transactionprocessor.models.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;


@Log4j2
@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(InvalidFileTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidFileTypeException(
            InvalidFileTypeException exception
    ){
        log.error("Invalid file type", exception);
        return buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ItemsEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEmptyItemsException(
            ItemsEmptyException exception
    ){
        log.error("Empty Items.", exception);
        return buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidFileException(
            InvalidFileException exception
    ){
        log.error("Invalid file.", exception);
        return buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMissingParams(
            MissingServletRequestPartException exception
    ){
        log.error("Missing fields", exception);
        return buildErrorResponse("Required parameter '"+ exception.getRequestPartName()+"' is missing.", HttpStatus.BAD_REQUEST);
    }

        private ResponseEntity<ErrorResponse> buildErrorResponse(
                String message,
                HttpStatus httpStatus
        ) {
            ErrorResponse errorResponse = new ErrorResponse(
                    httpStatus.value(),
                    message
            );

            return ResponseEntity.status(httpStatus).body(errorResponse);
        }


}
