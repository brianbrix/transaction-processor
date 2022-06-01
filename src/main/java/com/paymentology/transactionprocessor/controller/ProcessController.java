package com.paymentology.transactionprocessor.controller;

import com.paymentology.transactionprocessor.models.Response;
import com.paymentology.transactionprocessor.services.ProcessFilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api")
@Log4j2
public class ProcessController {
    private final ProcessFilesService processFilesService;
    @PostMapping(value = "upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Response> compareFiles(@RequestParam("file1") MultipartFile multipartFile, @RequestParam("file2") MultipartFile multipartFile2) throws IOException, IllegalAccessException {
        return processFilesService.upload(multipartFile, multipartFile2);
    }
    @GetMapping("/matches")
    Map<String, Collection> getMatches() throws IllegalAccessException {
        return processFilesService.getMatches();
    }
}
