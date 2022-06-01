package com.paymentology.transactionprocessor.services;

import com.paymentology.transactionprocessor.models.Response;
import com.paymentology.transactionprocessor.models.Transaction;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public interface ProcessFilesService {
    Map<String, Response> process(String path1, String path2) throws FileNotFoundException, IllegalAccessException;
    List<Map> getMatches() throws IllegalAccessException;

}
