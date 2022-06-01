package com.paymentology.transactionprocessor.services.impl;

import com.paymentology.transactionprocessor.exceptions.ItemsEmptyException;
import com.paymentology.transactionprocessor.models.CompleteFail;
import com.paymentology.transactionprocessor.models.PartialMatch;
import com.paymentology.transactionprocessor.models.Response;
import com.paymentology.transactionprocessor.models.Transaction;
import com.paymentology.transactionprocessor.services.ProcessFilesService;
import com.paymentology.transactionprocessor.services.ReadFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class ProcessFilesServiceImpl implements ProcessFilesService {
    String tmpdir = System.getProperty("java.io.tmpdir");
    private final ReadFileService readFileService;
    private Map<String, Response> responseMap=new HashMap<>();
    private List<Transaction> list1NoMatch= new ArrayList<>();
    private List<Transaction> list2NoMatch= new ArrayList<>();
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
    public Map<String, Response> upload(MultipartFile multipartFile1, MultipartFile multipartFile2) throws IOException {
        log.info("UPLOADING");
        Path path1 = Files.createTempFile(multipartFile1.getOriginalFilename(),".csv");
        Path path2 = Files.createTempFile(multipartFile2.getOriginalFilename(),".csv");
        try {
            log.info("Temp file path: {},{}" , path1, path2);
            multipartFile1.transferTo( path1.toFile());
            multipartFile2.transferTo(path2.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
        var res = process(path1.toString(),path2.toString());
        Files.delete(path1);
        Files.delete(path2);
        return res;
    }

    @Override
    public  Map<String,Collection> getMatches() throws IllegalAccessException {
        if (list2NoMatch.isEmpty() && list1NoMatch.isEmpty())
        {
            throw new ItemsEmptyException("No Items to match.Please upload two files.");
        }
        Map<String,Collection> result = new HashMap<>();
        List<CompleteFail> completeFails = new ArrayList<>();
        Set<PartialMatch> partialMatches = new HashSet<>();
        OUTER_LOOP: for (Transaction transaction : list1NoMatch) {
            var orTransaction = list2NoMatch.stream().filter(transaction2 -> transaction2.getTransactionId().equals(transaction.getTransactionId())).findFirst().orElse(null);
            if (!Objects.isNull(orTransaction)) {
                PartialMatch partialMatch = new PartialMatch();
                Field[] fields = Transaction.class.getDeclaredFields();
                for (Field field : fields) {
                    Object value1 = field.get(transaction);
                    Object value2 = field.get(orTransaction);
                    var similarity = findSimilarity(String.valueOf(value1), String.valueOf(value2));
                    if (field.getName().equals("transactionAmount") || field.getName().equals("walletReference")) {
                        if (similarity < 1) {
                            completeFails.add(new CompleteFail(transaction,  orTransaction));
                            continue OUTER_LOOP;
                        }


                    } else if (field.getName().equals("transactionDate")) {
                        if (similarity > 0.9) {
                            partialMatch.setTransaction(transaction);
                            partialMatch.setTransaction2(orTransaction);
                            partialMatch.getFields().add("transactionDate");
                        }


                    } else if (field.getName().equals("transactionNarrative")) {
                        if (similarity > 0.4) {
                            partialMatch.setTransaction(transaction);
                            partialMatch.setTransaction2(orTransaction);
                            partialMatch.getFields().add("transactionNarrative");
                        }

                    }
                }
                if (partialMatch.getTransaction()!=null)
                {
                    partialMatches.add(partialMatch);
                }
                else
                {
                    completeFails.add(new CompleteFail( transaction,  orTransaction));

                }


            } else {
                completeFails.add(new CompleteFail( transaction,  new Transaction()));
            }

        }
        List<CompleteFail> remainingLst2 = new ArrayList<>();
        list2NoMatch.forEach(match-> {
            if (completeFails.stream().noneMatch(b->b.getTransaction2().equals(match)) && partialMatches.stream().noneMatch(b->b.getTransaction2().equals(match)))
            {
                remainingLst2.add(new CompleteFail( new Transaction(), match));
            }

        });
        log.info("Remaining:{}",remainingLst2);
        completeFails.addAll(remainingLst2);
        result.put("completeFails", completeFails);
        result.put("partialMatches", partialMatches);
        log.info("COMPLETE: {}",completeFails.size());
        log.info("PARTIAL: {}",partialMatches.size());
        log.info("RESULT:{}", result);
        return result;
    }
    private double findSimilarity(String string1, String string2)
    {
        JaroWinklerSimilarity distance = new JaroWinklerSimilarity();
        return distance.apply(string1, string2);

    }
}
