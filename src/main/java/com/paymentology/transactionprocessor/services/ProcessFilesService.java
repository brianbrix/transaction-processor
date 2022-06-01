package com.paymentology.transactionprocessor.services;

import com.paymentology.transactionprocessor.models.Response;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProcessFilesService {
    Map<String, Response> process(String path1, String path2) throws FileNotFoundException, IllegalAccessException;
    Map<String, Collection> getMatches() throws IllegalAccessException;

}
