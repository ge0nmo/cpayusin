package com.cpayusin.controller;

import com.cpayusin.member.domain.Member;
import com.cpayusin.vote.controller.port.VoteFacade;
import com.cpayusin.vote.controller.VoteController;
import com.cpayusin.setup.RestDocsSetup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteController.class)
class VoteControllerTest extends RestDocsSetup
{
    @MockBean
    private VoteFacade voteFacade;


    @Test
    void votePost() throws Exception
    {
        // given
        Long postId = 1L;
        given(voteFacade.votePost(any(Member.class), any(Long.class))).willReturn(true);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/vote/post/{postId}", postId)
                        .with(csrf())
                        .with(user(memberDetails))
                );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("좋아요 성공"))
                .andDo(document("vote-post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 고유 식별 아이디")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.STRING).description("결과")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response = " + resultActions.andReturn().getResponse().getContentAsString());
    }

}