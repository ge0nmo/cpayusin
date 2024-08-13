package com.cpayusin.controller;

import com.cpayusin.facade.CommentFacade;
import com.cpayusin.global.dto.PageInfo;
import com.cpayusin.model.Member;
import com.cpayusin.payload.request.comment.CommentCreateRequest;
import com.cpayusin.payload.request.comment.CommentUpdateRequest;
import com.cpayusin.payload.response.GlobalResponse;
import com.cpayusin.payload.response.comment.*;
import com.cpayusin.service.CommentService;
import com.cpayusin.setup.RestDocsSetup;
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

    @MockBean
    private CommentFacade commentFacade;

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

        given(commentFacade.saveComment(any(CommentCreateRequest.class), any(Member.class))).willReturn(response);

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
                .boardId(1L)
                .boardName("게시판 1")
                .postId(1L)
                .postTitle("첫번째 게시글")
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
                                fieldWithPath("data.boardId").type(JsonFieldType.NUMBER).description("게시판 식별 번호"),
                                fieldWithPath("data.boardName").type(JsonFieldType.STRING).description("게시판 제목"),

                                fieldWithPath("data.postId").type(JsonFieldType.NUMBER).description("게시글 식별 번호"),
                                fieldWithPath("data.postTitle").type(JsonFieldType.STRING).description("게시글 제목"),

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
    void getCommentsByPostId() throws Exception {
        // given
        Long postId = 1L;

        CommentChildrenResponse childrenResponse1 = CommentChildrenResponse.builder()
                .id(9L)
                .text("댓글 테스트 4 - 1")
                .voteCount(0)
                .voteStatus(false)
                .isRemoved(false)
                .memberId(4L)
                .memberName("운영자21")
                .parentId(4L)
                .createdAt(LocalDateTime.of(2024, 7, 28, 1, 15))
                .build();

        List<CommentChildrenResponse> childrenResponseList = List.of(childrenResponse1);

        CommentResponse response1 = CommentResponse.builder()
                .id(4L)
                .text("댓글 테스트 5")
                .voteStatus(false)
                .voteCount(0)
                .memberId(4L)
                .memberName("운영자21")
                .memberProfile(null)
                .isRemoved(false)
                .createdAt(LocalDateTime.of(2024, 7, 28, 1, 13))
                .children(childrenResponseList)
                .build();

        List<CommentResponse> responseList = List.of(response1);

        CommentMultiResponse multiResponse = new CommentMultiResponse();
        multiResponse.setPostId(postId);
        multiResponse.setPostTitle("테스트1?");
        multiResponse.setBoardId(1L);
        multiResponse.setBoardName("테스트123");
        multiResponse.setComments(responseList);

        PageInfo pageInfo = new PageInfo(1, 10, 1, 1);
        GlobalResponse<CommentMultiResponse> globalResponse = new GlobalResponse<>(multiResponse, pageInfo);

        given(commentService.getCommentsByPostId(any(Long.class), any(Member.class), any())).willReturn(globalResponse);

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/comment")
                        .param("postId", postId.toString())
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
                                fieldWithPath("data.postId").type(JsonFieldType.NUMBER).description("게시글 고유 식별 번호"),
                                fieldWithPath("data.postTitle").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("data.boardId").type(JsonFieldType.NUMBER).description("게시판 고유 식별 번호"),
                                fieldWithPath("data.boardName").type(JsonFieldType.STRING).description("게시판 이름"),
                                fieldWithPath("data.comments").type(JsonFieldType.ARRAY).description("댓글 목록"),
                                fieldWithPath("data.comments[].id").type(JsonFieldType.NUMBER).description("댓글 고유 식별 번호"),
                                fieldWithPath("data.comments[].text").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("data.comments[].voteCount").type(JsonFieldType.NUMBER).description("추천수"),
                                fieldWithPath("data.comments[].voteStatus").type(JsonFieldType.BOOLEAN).description("추천 여부"),
                                fieldWithPath("data.comments[].isRemoved").type(JsonFieldType.BOOLEAN).description("삭제 여부"),
                                fieldWithPath("data.comments[].memberId").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호"),
                                fieldWithPath("data.comments[].memberName").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("data.comments[].memberProfile").type(JsonFieldType.STRING).description("유저 프로필 사진").optional(),
                                fieldWithPath("data.comments[].createdAt").type(JsonFieldType.STRING).description("댓글 생성 시간"),
                                fieldWithPath("data.comments[].children").type(JsonFieldType.ARRAY).description("하위 댓글 리스트").optional(),
                                fieldWithPath("data.comments[].children[].id").type(JsonFieldType.NUMBER).description("하위 댓글 고유 식별 번호").optional(),
                                fieldWithPath("data.comments[].children[].text").type(JsonFieldType.STRING).description("하위 댓글 내용").optional(),
                                fieldWithPath("data.comments[].children[].voteCount").type(JsonFieldType.NUMBER).description("하위 댓글 추천수").optional(),
                                fieldWithPath("data.comments[].children[].voteStatus").type(JsonFieldType.BOOLEAN).description("하위 댓글 추천 여부").optional(),
                                fieldWithPath("data.comments[].children[].isRemoved").type(JsonFieldType.BOOLEAN).description("하위 댓글 삭제 여부").optional(),
                                fieldWithPath("data.comments[].children[].memberId").type(JsonFieldType.NUMBER).description("하위 댓글 작성자 ID").optional(),
                                fieldWithPath("data.comments[].children[].memberName").type(JsonFieldType.STRING).description("댓글 작성자 이름").optional(),
                                fieldWithPath("data.comments[].children[].memberProfile").type(JsonFieldType.STRING).description("유저 프로필 사진").optional(),
                                fieldWithPath("data.comments[].children[].parentId").type(JsonFieldType.NUMBER).description("상위 댓글 고유 식별 번호").optional(),
                                fieldWithPath("data.comments[].children[].createdAt").type(JsonFieldType.STRING).description("댓글 생성 시간").optional()
                        ).and(pageInfoResponseFields())
                ));

        System.out.println("response: " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void getAllCommentsForProfile() throws Exception
    {
        // given
        Long postId = 1L;

        CommentResponseForProfile response1 = CommentResponseForProfile.builder()
                .postId(1L)
                .postTitle("첫번째 게시글")
                .boardId(1L)
                .boardName("게시판")
                .commentId(1L)
                .postId(postId)
                .text("댓글 1")
                .voteCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        CommentResponseForProfile response2 = CommentResponseForProfile.builder()
                .postId(1L)
                .postTitle("첫번째 게시글")
                .boardId(1L)
                .boardName("게시판")
                .commentId(2L)
                .postId(postId)
                .text("댓글 2")
                .voteCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        CommentResponseForProfile response3 = CommentResponseForProfile.builder()
                .postId(1L)
                .postTitle("첫번째 게시글")
                .boardId(1L)
                .boardName("게시판")
                .commentId(3L)
                .postId(postId)
                .text("댓글 3")
                .voteCount(0)
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
                                fieldWithPath("data[].boardId").type(JsonFieldType.NUMBER).description("게시판 고유 식별 번호"),
                                fieldWithPath("data[].boardName").type(JsonFieldType.STRING).description("게시판 제목"),
                                fieldWithPath("data[].postId").type(JsonFieldType.NUMBER).description("게시글 고유 식별 번호"),
                                fieldWithPath("data[].postTitle").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("data[].commentId").type(JsonFieldType.NUMBER).description("댓글 고유 식별 번호"),
                                fieldWithPath("data[].text").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("data[].voteCount").type(JsonFieldType.NUMBER).description("댓글 추천 수"),
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

        given(commentFacade.deleteComment(any(Long.class), any(Member.class))).willReturn(true);

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