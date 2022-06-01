package com.paymentology.transactionprocessor.services.impl;

import com.opencsv.exceptions.CsvValidationException;
import com.paymentology.transactionprocessor.models.Transaction;
import com.paymentology.transactionprocessor.services.ReadFileService;
import com.univocity.parsers.common.fields.ColumnMapper;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.*;

@Log4j2
@Service
public class ReadFileServiceImpl implements ReadFileService {
    private BeanListProcessor<Transaction> getProcessor()
    {
        BeanListProcessor<Transaction> processor = new BeanListProcessor<>(Transaction.class);
        ColumnMapper mapper = processor.getColumnMapper();
        Map<String, String> colNames = new HashMap<>();
        colNames.put("profileName","ProfileName");
        colNames.put("transactionDate","TransactionDate");
        colNames.put("transactionAmount","TransactionAmount");
        colNames.put("transactionNarrative","TransactionNarrative");
        colNames.put("transactionDescription","TransactionDescription");
        colNames.put("transactionId","TransactionID");
        colNames.put("transactionType","TransactionType");
        colNames.put("walletReference","WalletReference");
        mapper.attributesToColumnNames(colNames);
    return processor;
    }
    @Override
    public List<Transaction> readFile(String path) throws FileNotFoundException {
        log.info("PATH:{}", path);
        var processor = getProcessor();
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setProcessor(processor);
        parserSettings.getFormat().setDelimiter(',');
        parserSettings.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(new FileReader(path));

        return processor.getBeans();
    }

}
