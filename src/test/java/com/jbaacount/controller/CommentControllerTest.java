package com.jbaacount.controller;

import com.jbaacount.model.Member;
import com.jbaacount.payload.request.comment.CommentCreateRequest;
import com.jbaacount.payload.request.comment.CommentUpdateRequest;
import com.jbaacount.payload.response.comment.*;
import com.jbaacount.service.CommentService;
import com.jbaacount.setup.RestDocsSetup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest extends RestDocsSetup
{
    @MockBean
    private CommentService commentService;

    @Test
    void saveComment() throws Exception
    {
        // given
        Long postId = 1L;
        Long parentCommentId = 1L;
        Long commentId = 3L;
        String text = "댓글 테스트 123";

        CommentCreateRequest request = CommentCreateRequest.builder()
                .postId(postId)
                .parentCommentId(parentCommentId)
                .text(text)
                .build();

        CommentCreatedResponse response = CommentCreatedResponse.builder()
                .id(commentId)
                .text(text)
                .createdAt(LocalDateTime.now())
                .build();

        String requestBody = objectMapper.writeValueAsString(request);

        given(commentService.saveComment(any(CommentCreateRequest.class), any(Member.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/comment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user(memberDetails))
                        .with(csrf()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").value(commentId))
                .andExpect(jsonPath("data.text").value(text))
                .andDo(document("create comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 고유 식별 번호"),
                                fieldWithPath("text").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).description("상위 댓글 식별 번호").optional()
                        ),

                        responseFields(
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("댓글 고유 식별 번호"),
                                fieldWithPath("data.text").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("댓글 작성 시간")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response: " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void updateComment() throws Exception
    {
        // given
        Long commentId = 1L;
        String text = "댓글 수정 테스트";

        CommentUpdateRequest request = CommentUpdateRequest.builder()
                .text(text)
                .build();

        CommentUpdateResponse response = CommentUpdateResponse.builder()
                .id(commentId)
                .text(text)
                .modifiedAt(LocalDateTime.now())
                .build();

        String requestBody = objectMapper.writeValueAsString(request);

        given(commentService.updateComment(any(CommentUpdateRequest.class), any(Long.class), any(Member.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(patch("/api/v1/comment/update/{commentId}", commentId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user(memberDetails)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").value(commentId))
                .andExpect(jsonPath("data.text").value(text))
                .andDo(document("update comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        pathParameters(
                                parameterWithName("commentId").description("댓글 식별 내용")
                        ),

                        requestFields(
                                fieldWithPath("text").type(JsonFieldType.STRING).description("댓글 내용")
                        ),

                        responseFields(
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("댓글 고유 식별 번호"),
                                fieldWithPath("data.text").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("댓글 수정 시간")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response: " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void getComment() throws Exception
    {
        // given
        Long memberId = 1L;
        String memberNickname = "관리자";
        Long parentCommentId = 1L;
        String text = "댓글 조회 테스트";
        Long commentId = 3L;

        CommentSingleResponse response = CommentSingleResponse.builder()
                .commentId(commentId)
                .text(text)
                .memberId(memberId)
                .memberProfile(URL)
                .parentId(parentCommentId)
                .isRemoved(false)
                .nickname(memberNickname)
                .voteCount(2)
                .voteStatus(false)
                .createdAt(LocalDateTime.now())
                .build();

        given(commentService.getCommentSingleResponse(any(Long.class), any(Member.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/comment/{commentId}", commentId)
                        .with(user(memberDetails))
                        .with(csrf()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.commentId").value(commentId))
                .andExpect(jsonPath("data.text").value(text))
                .andExpect(jsonPath("data.memberId").value(memberId))
                .andExpect(jsonPath("data.nickname").value(memberNickname))
                .andExpect(jsonPath("data.parentId").value(parentCommentId))
                .andDo(document("comment-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId").description("댓글 식별 내용")
                        ),

                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("data.memberProfile").type(JsonFieldType.STRING).description("유저 프로필 사진").optional(),

                                fieldWithPath("data.commentId").type(JsonFieldType.NUMBER).description("댓글 고유 식별 번호"),
                                fieldWithPath("data.parentId").type(JsonFieldType.NUMBER).description("상위 댓글 고유 식별 번호").optional(),
                                fieldWithPath("data.text").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("data.voteCount").type(JsonFieldType.NUMBER).description("추천 수"),
                                fieldWithPath("data.voteStatus").type(JsonFieldType.BOOLEAN).description("추천 여부"),
                                fieldWithPath("data.isRemoved").type(JsonFieldType.BOOLEAN).description("삭제 여부"),
                                fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("댓글 생성 시간")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response: " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void getAllComments() throws Exception
    {
        // given
        Long postId = 1L;

        CommentChildrenResponse childrenResponse1 = CommentChildrenResponse.builder()
                .id(2L)
                .text("네 그렇습니다.")
                .voteCount(100)
                .voteStatus(true)
                .isRemoved(false)
                .memberId(3L)
                .memberName("관리자")
                .parentId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        CommentChildrenResponse childrenResponse2 = CommentChildrenResponse.builder()
                .id(4L)
                .text("와.. 대박")
                .voteCount(0)
                .voteStatus(false)
                .isRemoved(false)
                .memberId(3L)
                .memberName("유저1")
                .parentId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        List<CommentChildrenResponse> childrenResponseList = List.of(childrenResponse1, childrenResponse2);

        CommentMultiResponse response1 = CommentMultiResponse.builder()
                .id(1L)
                .text("지구는 평평한가요?")
                .voteStatus(false)
                .voteCount(0)
                .memberId(1L)
                .memberName("홍길동")
                .memberProfile(URL)
                .isRemoved(false)
                .createdAt(LocalDateTime.now())
                .children(childrenResponseList)
                .build();

        CommentMultiResponse response2 = CommentMultiResponse.builder()
                .id(6L)
                .text("댓글 테스트 2")
                .voteStatus(false)
                .voteCount(0)
                .memberId(5L)
                .memberName("김아무개")
                .memberProfile(URL)
                .isRemoved(false)
                .createdAt(LocalDateTime.now())
                .build();

        List<CommentMultiResponse> responseList = List.of(response1, response2);
        given(commentService.getAllCommentByPostId(any(Long.class), any(Member.class))).willReturn(responseList);

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/comment")
                        .param("postId", postId + "")
                        .with(user(memberDetails)));


        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document("allComment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("postId").description("게시글 고유 식별 번호")
                        ),
                        responseFields(
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("댓글 고유 식별 번호"),
                                fieldWithPath("data[].text").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("data[].voteCount").type(JsonFieldType.NUMBER).description("추천수"),
                                fieldWithPath("data[].voteStatus").type(JsonFieldType.BOOLEAN).description("추천 여부"),
                                fieldWithPath("data[].isRemoved").type(JsonFieldType.BOOLEAN).description("삭제 여부"),

                                fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호"),
                                fieldWithPath("data[].memberName").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("data[].memberProfile").type(JsonFieldType.STRING).description("유저 프로필 사진").optional(),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("댓글 생성 시간"),
                                fieldWithPath("data[].children").type(JsonFieldType.ARRAY).description("하위 댓글 리스트").optional(),

                                fieldWithPath("data[].children[].id").type(JsonFieldType.NUMBER).description("하위 댓글 고유 식별 번호").optional(),
                                fieldWithPath("data[].children[].text").type(JsonFieldType.STRING).description("하위 댓글 내용").optional(),
                                fieldWithPath("data[].children[].voteCount").type(JsonFieldType.NUMBER).description("하위 댓글 추천수").optional(),
                                fieldWithPath("data[].children[].voteStatus").type(JsonFieldType.BOOLEAN).description("하위 댓글 추천 여부").optional(),
                                fieldWithPath("data[].children[].isRemoved").type(JsonFieldType.BOOLEAN).description("하위 댓글 삭제 여부").optional(),

                                fieldWithPath("data[].children[].memberId").type(JsonFieldType.NUMBER).description("하위 댓글 작성자 ID").optional(),
                                fieldWithPath("data[].children[].memberName").type(JsonFieldType.STRING).description("댓글 작성자 이름").optional(),
                                fieldWithPath("data[].children[].memberProfile").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호").optional(),
                                fieldWithPath("data[].children[].parentId").type(JsonFieldType.NUMBER).description("상위 댓글 고유 식별 번호").optional(),
                                fieldWithPath("data[].children[].createdAt").type(JsonFieldType.STRING).description("댓글 생성 시간").optional()

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response: " + resultActions.andReturn().getResponse().getContentAsString());

    }

    @Test
    void getAllCommentsForProfile() throws Exception
    {
        // given
        Long postId = 1L;

        CommentResponseForProfile response1 = CommentResponseForProfile.builder()
                .id(1L)
                .postId(postId)
                .text("댓글 1")
                .voteCount(0)
                .isRemoved(false)
                .createdAt(LocalDateTime.now())
                .build();

        CommentResponseForProfile response2 = CommentResponseForProfile.builder()
                .id(2L)
                .postId(postId)
                .text("댓글 2")
                .voteCount(0)
                .isRemoved(false)
                .createdAt(LocalDateTime.now())
                .build();

        CommentResponseForProfile response3 = CommentResponseForProfile.builder()
                .id(3L)
                .postId(postId)
                .text("댓글 3")
                .voteCount(0)
                .isRemoved(false)
                .createdAt(LocalDateTime.now())
                .build();

        Integer page = 1;
        Integer size = 10;

        List<CommentResponseForProfile> responseList = List.of(response1, response2, response3);
        Pageable pageable = PageRequest.of(page, size);

        Page<CommentResponseForProfile> pageResponse = new PageImpl<>(responseList, pageable, responseList.size());

        given(commentService.getAllCommentsForProfile(any(Member.class), any(Pageable.class))).willReturn(pageResponse);

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/profile/my-comments")
                        .param("page", page.toString())
                        .param("size", size.toString())
                        .with(user(memberDetails)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document("profile-comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 당 데이터 개수")
                        ),

                        responseFields(
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("댓글 고유 식별 번호"),
                                fieldWithPath("data[].postId").type(JsonFieldType.NUMBER).description("게시글 고유 식별 번호"),
                                fieldWithPath("data[].text").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("data[].voteCount").type(JsonFieldType.NUMBER).description("댓글 추천 수"),
                                fieldWithPath("data[].isRemoved").type(JsonFieldType.BOOLEAN).description("댓글 삭제 여부"),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("댓글 생성 날짜")

                        ).andWithPrefix("", pageInfoResponseFields())
                ));

        System.out.println("response: " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void deleteComment() throws Exception
    {
        // given
        Long commentId = 1L;

        given(commentService.deleteComment(any(Long.class), any(Member.class))).willReturn(true);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/api/v1/comment/delete/{comment-id}", commentId.toString())
                        .with(csrf())
                        .with(user(memberDetails)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document("delete-comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("comment-id").description("댓글 고유 식별 번호")
                        ),

                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.STRING).description("데이터")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response: " + resultActions.andReturn().getResponse().getContentAsString());
    }
}