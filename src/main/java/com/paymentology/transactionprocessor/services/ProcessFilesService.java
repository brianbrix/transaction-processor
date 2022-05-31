package com.paymentology.transactionprocessor.services;

import com.paymentology.transactionprocessor.models.Transaction;

import java.io.FileNotFoundException;
import java.util.List;

public interface ProcessFilesService {
    List<Transaction> process(String path1, String path2) throws FileNotFoundException;
}
