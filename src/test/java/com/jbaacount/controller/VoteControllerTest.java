package com.jbaacount.controller;

import com.jbaacount.model.Member;
import com.jbaacount.service.VoteService;
import com.jbaacount.setup.RestDocsSetup;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteController.class)
class VoteControllerTest extends RestDocsSetup
{
    @MockBean
    private VoteService voteService;

    @Test
    void votePost() throws Exception
    {
        // given
        Long postId = 1L;
        given(voteService.votePost(any(Member.class), any(Long.class))).willReturn(true);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/vote/post/{postId}", postId)
                        .with(csrf())
                        .with(user(memberDetails))
                        //.param("postId", postId + "")
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

    @Test
    void voteComment() throws Exception
    {
        // given
        Long commentId = 1L;
        given(voteService.voteComment(any(Member.class), any(Long.class))).willReturn(true);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/vote/comment/{commentId}", commentId)
                                .with(csrf())
                                .with(user(memberDetails))
                        //.param("commentId", commentId + "")
                );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("좋아요 성공"))
                .andDo(document("vote-comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId").description("게시글 고유 식별 아이디")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.STRING).description("결과")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response = " + resultActions.andReturn().getResponse().getContentAsString());
    }
}