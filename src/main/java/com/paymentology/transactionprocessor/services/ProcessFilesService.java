package com.paymentology.transactionprocessor.services;

import com.paymentology.transactionprocessor.models.Response;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProcessFilesService {
    Map<String, Response> process(String path1, String path2) throws FileNotFoundException, IllegalAccessException;
    Map<String, Response> upload(MultipartFile multipartFile1, MultipartFile multipartFile2) throws IOException, IllegalAccessException;
    Map<String, Collection> getMatches() throws IllegalAccessException;

}
