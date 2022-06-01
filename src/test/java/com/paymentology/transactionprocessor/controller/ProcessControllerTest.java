package com.paymentology.transactionprocessor.controller;

import com.paymentology.transactionprocessor.services.ProcessFilesService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileInputStream;


import static org.assertj.core.api.Assertions.assertThat;


@Log4j2
@WebMvcTest
//@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ProcessControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProcessFilesService processFilesService;
    @Autowired
    private ProcessController processController;


    @Test
    void contextLoads() throws Exception {
        assertThat(processController).isNotNull();

    }
    @Test
    void testUpload() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("test.xlsx", new FileInputStream(new File("src/main/resources/testfiles/test.csv")));
        MockMultipartFile multipartFile2 = new MockMultipartFile("test.xlsx", new FileInputStream(new File("src/main/resources/testfiles/test2.csv")));
        var res =mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
                        .file("file1",multipartFile.getBytes())
                        .file("file2",multipartFile2.getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        log.info("Res: {}",res.getResponse().getContentAsString());

    }


}