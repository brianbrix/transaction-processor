package com.paymentology.transactionprocessor.services;

import com.paymentology.transactionprocessor.services.impl.ReadFileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ReadFileServiceTest {
    @Autowired
    ReadFileServiceImpl readFileService;


}