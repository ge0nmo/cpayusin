package com.jbaacount.controller;

import com.jbaacount.payload.response.VisitorResponse;
import com.jbaacount.service.VisitorService;
import com.jbaacount.setup.RestDocsSetup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitorController.class)
class VisitorControllerTest extends RestDocsSetup
{
    @MockBean
    private VisitorService visitorService;

    @Test
    void getVisitors() throws Exception
    {
        // given
        VisitorResponse response = VisitorResponse.builder()
                .today(2L)
                .yesterday(10L)
                .total(300L)
                .build();

        given(visitorService.getVisitorResponse()).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/visitor"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.yesterday").value(response.getYesterday()))
                .andExpect(jsonPath("$.data.today").value(response.getToday()))
                .andExpect(jsonPath("$.data.total").value(response.getTotal()))

                .andDo(document("get visitor",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.yesterday").type(JsonFieldType.NUMBER).description("어제 방문자 수"),
                                fieldWithPath("data.today").type(JsonFieldType.NUMBER).description("오늘 방문자 수"),
                                fieldWithPath("data.total").type(JsonFieldType.NUMBER).description("총 방문자 수")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));
    }
}