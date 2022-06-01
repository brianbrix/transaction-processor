package com.paymentology.transactionprocessor.services.impl;

import com.paymentology.transactionprocessor.models.Response;
import com.paymentology.transactionprocessor.models.Transaction;
import com.paymentology.transactionprocessor.services.ProcessFilesService;
import com.paymentology.transactionprocessor.services.ReadFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class ProcessFilesServiceImpl implements ProcessFilesService {
    private final ReadFileService readFileService;
    private Map<String, Response> responseMap=new HashMap<>();
    private List<Transaction> list1NoMatch;
    private List<Transaction> list2NoMatch;
    @Override
    public Map<String, Response> process(String path1, String path2) throws FileNotFoundException {
        var tranList1 =new ArrayList<>(readFileService.readFile(path1).stream().collect(Collectors.toConcurrentMap(Transaction::getTransactionId, Function.identity(), (p, q) -> p)).values());
        var tranList2 = new ArrayList<>(readFileService.readFile(path2).stream().collect(Collectors.toConcurrentMap(Transaction::getTransactionId, Function.identity(), (p, q) -> p)).values());
        tranList1.sort(Comparator.comparing(Transaction::getTransactionId));
        tranList2.sort(Comparator.comparing(Transaction::getTransactionId));
        log.info("SIZES: {},{}", tranList1.size(), tranList2.size());
        var fullMatchList =  new ArrayList<>(CollectionUtils.intersection(tranList1, tranList2));
        list1NoMatch = new ArrayList<>(CollectionUtils.disjunction(fullMatchList, tranList1));
        list2NoMatch = new ArrayList<>(CollectionUtils.disjunction(fullMatchList, tranList2));
        var res1 = Response.builder()
                .matchingRecords(fullMatchList.size())
                .unmatchedRecords(list1NoMatch.size())
                .totalRecords(tranList1.size())
                .build();
        var res2 = Response.builder()
                .matchingRecords(fullMatchList.size())
                .unmatchedRecords(list2NoMatch.size())
                .totalRecords(tranList2.size())
                .build();
        responseMap.put("file1", res1);
        responseMap.put("file2", res2);

        log.info("Full Match: {}", fullMatchList.size());
        log.info("Non Full 1 Match: {}", list1NoMatch.size());
        log.info("Non Full 2 Match: {}", list2NoMatch.size());

        return responseMap;
    }
    @Override
    public List<Map> getMatches() throws IllegalAccessException {
        List<Map> result = new ArrayList<>();
        Map<Integer,List<Transaction>> completelyFailing = new HashMap<>();
        Map<String,List<Transaction>> partiallyFailing = new HashMap<>();
        completelyFailing.put(1, new ArrayList<>());
        completelyFailing.put(2, new ArrayList<>());
        partiallyFailing.put("transactionDate1", new ArrayList<>());
        partiallyFailing.put("transactionDate2", new ArrayList<>());
        partiallyFailing.put("transactionNarrative1", new ArrayList<>());
        partiallyFailing.put("transactionNarrative2", new ArrayList<>());

        OUTER_LOOP: for (Transaction transaction: list1NoMatch)
        {
            var orTransaction = list2NoMatch.stream().filter(transaction2-> transaction2.getTransactionId().equals(transaction.getTransactionId())).findFirst().orElse(null);
            if (!Objects.isNull(orTransaction)) {
                log.info(orTransaction.getTransactionId());
                Field[] fields = Transaction.class.getDeclaredFields();
                for (Field field: fields) {
                    Object value1 = field.get(transaction);
                    Object value2 = field.get(orTransaction);
                    var similarity = findSimilarity(String.valueOf(value1), String.valueOf(value2));
                    if (field.getName().equals("transactionAmount")) {
                        if (similarity < 1) {
                            completelyFailing.get(1).add(transaction);
                            completelyFailing.get(2).add(orTransaction);
                            continue OUTER_LOOP;
                        }
                    } else if (field.getName().equals("walletReference")) {
                        if (similarity < 1) {
                            completelyFailing.get(1).add(transaction);
                            completelyFailing.get(2).add(orTransaction);
                            continue OUTER_LOOP;
                        }

                    } else {
                        if (field.getName().equals("transactionDate")) {
                            if (similarity < 0.9) {
                                completelyFailing.get(1).add(transaction);
                                completelyFailing.get(2).add(orTransaction);
                            }
                            else
                            {
                                partiallyFailing.get("transactionDate1").add(transaction);
                                partiallyFailing.get("transactionDate2").add(orTransaction);
                            }
                        }

                        else if (field.getName().equals("transactionNarrative")) {
                            if (similarity < 0.5) {
                                completelyFailing.get(1).add(transaction);
                                completelyFailing.get(2).add(orTransaction);
                            }
                            else
                            {
                                partiallyFailing.get("transactionNarrative1").add(transaction);
                                partiallyFailing.get("transactionNarrative2").add(orTransaction);
                            }
                        }
                    }
                }


            }
            else {
                log.info("Else,{}",transaction.getTransactionId());
                completelyFailing.get(1).add(transaction);
            }

        }
        var completeMissingIn2 = new ArrayList<>(CollectionUtils.disjunction(list2NoMatch, completelyFailing.get(2)));
        completelyFailing.get(2).addAll(completeMissingIn2);
        result.add(partiallyFailing);
        result.add(completelyFailing);
        completelyFailing.forEach((key, value)->log.info("FAILING:{}",value.size()));
        partiallyFailing.forEach((key, value)->log.info("PARTIAL FAILING:{}",value.size()));
        return result;
    }
    private double findSimilarity(String string1, String string2)
    {
        JaroWinklerSimilarity distance = new JaroWinklerSimilarity();
        return distance.apply(string1, string2);

    }
}
