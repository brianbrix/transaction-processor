package com.paymentology.transactionprocessor.services;

import com.opencsv.exceptions.CsvValidationException;
import com.paymentology.transactionprocessor.services.impl.ReadFileServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ReadFileServiceTest {
    @Autowired
    ReadFileServiceImpl readFileService;

    @Test
    void testReadFile() throws IOException {

        readFileService.readFile("testfiles/test.csv");
        readFileService.readFile("testfiles/test2.csv");
    }


}