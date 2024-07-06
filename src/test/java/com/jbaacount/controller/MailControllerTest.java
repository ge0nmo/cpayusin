package com.jbaacount.controller;

import com.jbaacount.payload.request.member.SendVerificationCodeRequest;
import com.jbaacount.service.MailService;
import com.jbaacount.setup.RestDocsSetup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MailController.class)
class MailControllerTest extends RestDocsSetup
{
    @MockBean
    private MailService mailService;

    @Test
    void sendVerificationCode() throws Exception
    {
        // given
        String email = "admin@gmail.com";
        String response = "인증코드가 발송되었습니다. 5분 내로 인증을 완료해주세요.";

        SendVerificationCodeRequest request = new SendVerificationCodeRequest(email);

        given(mailService.sendVerificationCode(any(SendVerificationCodeRequest.class)))
                .willReturn(response);

        String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/mail/send-verification")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(response))
                .andDo(document("send-email",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.STRING).description("결과")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response = " + resultActions.andReturn().getResponse().getContentAsString());
    }
}