package com.paymentology.transactionprocessor.services.impl;

import com.paymentology.transactionprocessor.services.ProcessFilesService;
import com.paymentology.transactionprocessor.services.ReadFileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;


@SpringBootTest
class ProcessFilesServiceImplTest {
    @Autowired
    ReadFileService readFileService;
    @Autowired
    ProcessFilesService processFilesService;
    @Test
    void testProcessFiles() throws FileNotFoundException {
        processFilesService.process("testfiles/test.csv","testfiles/test2.csv");
    }


}