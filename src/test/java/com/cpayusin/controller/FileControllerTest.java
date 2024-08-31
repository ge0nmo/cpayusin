package com.cpayusin.controller;

import com.cpayusin.file.controller.FileController;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.file.controller.response.FileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(FileController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class FileControllerTest
{
    @Autowired
    private MockMvc mvc;

    @MockBean
    private FileService fileService;

    @MockBean
    protected RedisTemplate<String, String> redisTemplate;

    @MockBean
    protected ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp()
    {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    void save() throws Exception
    {
        // given
        String url = "https://jbacunt.s3.ap-northst-2.amazonaws.com/post/99198ebb-107e-489c-91e2-61d2ea7febde.jpg";

        FileResponse fileResponse = new FileResponse();
        fileResponse.setUrl(url);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "<<jpg data>>".getBytes()
        );

        when(fileService.save(any(MockMultipartFile.class))).thenReturn(fileResponse);


        // when
        ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.multipart("/api/v1/file")
                        .file(file)
                        .with(csrf()));

        // then
        resultActions
                .andExpect(status().isCreated());

    }
}