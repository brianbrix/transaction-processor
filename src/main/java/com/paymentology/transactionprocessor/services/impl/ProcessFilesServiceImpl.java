package com.paymentology.transactionprocessor.services.impl;

import com.paymentology.transactionprocessor.models.Transaction;
import com.paymentology.transactionprocessor.services.ProcessFilesService;
import com.paymentology.transactionprocessor.services.ReadFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class ProcessFilesServiceImpl implements ProcessFilesService {
    private final ReadFileService readFileService;
    @Override
    public List<Transaction> process(String path1, String path2) throws FileNotFoundException {
        var tranList1 =new ArrayList<>(readFileService.readFile(path1).stream().collect(Collectors.toConcurrentMap(Transaction::getTransactionId, Function.identity(), (p, q) -> p)).values());
        var tranList2 = new ArrayList<>(readFileService.readFile(path2).stream().collect(Collectors.toConcurrentMap(Transaction::getTransactionId, Function.identity(), (p, q) -> p)).values());
        tranList1.sort(Comparator.comparing(Transaction::getTransactionId));
        tranList2.sort(Comparator.comparing(Transaction::getTransactionId));
        log.info("SIZES: {},{}", tranList1.size(), tranList2.size());
        var fullMatchList =  new ArrayList<>(CollectionUtils.intersection(tranList1, tranList2));
        var noFullMatchList = new ArrayList<>(CollectionUtils.disjunction(tranList1, tranList2));

        log.info("Full Match: {}", fullMatchList.size());
        log.info("Non Full Match: {}", noFullMatchList.size());
        for (Transaction transaction: noFullMatchList)
        {
            log.info(transaction);
//            var orTransaction = tranList2.stream().filter(transaction1 -> transaction1.getTransactionId().equals(transaction.getTransactionId())).findFirst().orElse(null);
//            if (!Objects.isNull(orTransaction)) {
//                Map<String>
//                var dif = findDifference(transaction.getTransactionDescription(), orTransaction.getTransactionDescription());
//                log.info("DIFF: {}", dif);
//            }
        }

        return null;
    }
    private double findDifference(String string1, String string2)
    {
//        log.i
        JaroWinklerSimilarity distance = new JaroWinklerSimilarity();

        return distance.apply(string1, string2);

    }
}
