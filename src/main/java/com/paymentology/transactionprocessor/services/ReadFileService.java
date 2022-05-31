package com.paymentology.transactionprocessor.services;

import com.paymentology.transactionprocessor.models.Transaction;

import java.io.FileNotFoundException;

import java.util.List;

public interface ReadFileService {
    List<Transaction> readFile(String path) throws FileNotFoundException;
}
