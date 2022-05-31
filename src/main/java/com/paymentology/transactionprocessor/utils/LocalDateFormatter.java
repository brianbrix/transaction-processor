package com.paymentology.transactionprocessor.utils;

import com.univocity.parsers.conversions.Conversion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class   LocalDateFormatter implements Conversion<String, LocalDateTime> {
    private final DateTimeFormatter formatter;

    public LocalDateFormatter(String[] args) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        if(args.length > 0){
            pattern = args[0];
        }
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public LocalDateTime execute(String input) {
        return LocalDateTime.parse(input, formatter);
    }

    @Override
    public String revert(LocalDateTime input) {
        return formatter.format(input);
    }
}
