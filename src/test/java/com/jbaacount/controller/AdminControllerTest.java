package com.jbaacount.controller;

import com.jbaacount.global.security.userdetails.MemberDetails;
import com.jbaacount.model.Board;
import com.jbaacount.model.Member;
import com.jbaacount.model.type.BoardType;
import com.jbaacount.payload.request.board.BoardCreateRequest;
import com.jbaacount.payload.request.board.BoardUpdateRequest;
import com.jbaacount.payload.request.board.CategoryUpdateRequest;
import com.jbaacount.payload.response.board.BoardChildrenResponse;
import com.jbaacount.payload.response.board.BoardCreateResponse;
import com.jbaacount.payload.response.board.BoardMenuResponse;
import com.jbaacount.service.BoardService;
import com.jbaacount.setup.RestDocsSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest extends RestDocsSetup
{
    @MockBean
    private BoardService boardService;


    Member member;
    Board board1;
    Board board2;
    Board childBoard1;
    MemberDetails memberDetails;


    @BeforeEach
    void setUp()
    {
        member = newMockMember(1L, "aa@naver.com", "test1", "ADMIN");
        memberDetails = new MemberDetails(member);

        board1 = newMockBoard(1L, "board1", 1);
        board2 = newMockBoard(2L, "board2", 2);

        childBoard1 = newMockBoard(3L, "child board", 1);
        childBoard1.setParent(board1);

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    void saveBoard_test() throws Exception
    {
        // given
        BoardCreateRequest request = new BoardCreateRequest();
        request.setIsAdminOnly(false);
        request.setName(board1.getName());

        String requestBody = objectMapper.writeValueAsString(request);

        BoardCreateResponse response = new BoardCreateResponse();
        response.setId(board1.getId());
        response.setIsAdminOnly(board1.getIsAdminOnly());
        response.setOrderIndex(board1.getOrderIndex());
        response.setName(board1.getName());

        given(boardService.createBoard(any(BoardCreateRequest.class), any(Member.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/admin/manage/board/create")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(memberDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                );


        // then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response body = " + responseBody);

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(request.getName()))
                .andDo(document("board-save",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("게시판 제목"),
                                fieldWithPath("isAdminOnly").type(JsonFieldType.BOOLEAN).description("관리자만 글을 쓸 수 있는지 여부"),
                                fieldWithPath("parentId").type(JsonFieldType.NUMBER).description("상위 게시판 아이디").optional()
                        ),

                        responseFields(
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시판 아이디"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("게시판 제목"),
                                fieldWithPath("data.isAdminOnly").type(JsonFieldType.BOOLEAN).description("관리자만 글을 쓸 수 있는지 여부"),
                                fieldWithPath("data.orderIndex").type(JsonFieldType.NUMBER).description("게시판 순서"),
                                fieldWithPath("data.parentId").type(JsonFieldType.NUMBER).description("상위 게시판 아이디").optional()

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));
    }

    @Test
    void updateBoard_test1() throws Exception
    {
        // given
        CategoryUpdateRequest categoryRequest1 = CategoryUpdateRequest.builder()
                .id(4L)
                .name("category1 after change")
                .orderIndex(1)
                .isAdminOnly(true)
                .build();

        CategoryUpdateRequest categoryRequest2 = CategoryUpdateRequest.builder()
                .id(5L)
                .name("category2 after change")
                .orderIndex(2)
                .isAdminOnly(false)
                .build();

        BoardUpdateRequest boardRequest1 = BoardUpdateRequest.builder()
                .id(1L)
                .name("board1 after change")
                .orderIndex(1)
                .isAdminOnly(true)
                .category(List.of(categoryRequest1, categoryRequest2))
                .build();


        BoardUpdateRequest boardRequest2 = BoardUpdateRequest.builder()
                .id(2L)
                .name("board2 after change")
                .orderIndex(2)
                .isAdminOnly(true)
                .build();

        BoardUpdateRequest boardRequest3 = BoardUpdateRequest.builder()
                .id(3L)
                .name("board3 after change")
                .orderIndex(3)
                .isAdminOnly(false)
                .build();

        List<BoardUpdateRequest> boardUpdateRequestList = List.of(boardRequest1, boardRequest2, boardRequest3);


        BoardMenuResponse response1 = BoardMenuResponse.builder()
                .id(boardRequest1.getId())
                .name(boardRequest1.getName())
                .orderIndex(boardRequest1.getOrderIndex())
                .isAdminOnly(boardRequest1.getIsAdminOnly())
                .type(BoardType.BOARD.getCode())
                .build();

        BoardChildrenResponse childrenResponse1 =  BoardChildrenResponse.builder()
                .id(categoryRequest1.getId())
                .name(categoryRequest1.getName())
                .orderIndex(categoryRequest1.getOrderIndex())
                .isAdminOnly(categoryRequest1.getIsAdminOnly())
                .type(BoardType.CATEGORY.getCode())
                .parentId(response1.getId())
                .build();

        BoardChildrenResponse childrenResponse2 =  BoardChildrenResponse.builder()
                .id(categoryRequest2.getId())
                .name(categoryRequest2.getName())
                .orderIndex(categoryRequest2.getOrderIndex())
                .isAdminOnly(categoryRequest2.getIsAdminOnly())
                .type(BoardType.CATEGORY.getCode())
                .parentId(response1.getId())
                .build();

        response1.setCategory(List.of(childrenResponse1, childrenResponse2));


        BoardMenuResponse response2 = BoardMenuResponse.builder()
                .id(boardRequest2.getId())
                .name(boardRequest2.getName())
                .orderIndex(boardRequest2.getOrderIndex())
                .isAdminOnly(boardRequest2.getIsAdminOnly())
                .type(BoardType.BOARD.getCode())
                .build();

        BoardMenuResponse response3 = BoardMenuResponse.builder()
                .id(boardRequest3.getId())
                .name(boardRequest3.getName())
                .orderIndex(boardRequest3.getOrderIndex())
                .isAdminOnly(boardRequest3.getIsAdminOnly())
                .type(BoardType.BOARD.getCode())
                .build();

        List<BoardMenuResponse> responseList = List.of(response1, response2, response3);

        given(boardService.bulkUpdateBoards(any(), any(Member.class))).willReturn(responseList);

        String requestBody = objectMapper.writeValueAsString(boardUpdateRequestList);

        // when
        ResultActions resultActions = mvc
                .perform(patch("/api/v1/admin/manage/update")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(memberDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        System.out.println("request body = " + requestBody);
        System.out.println("response body = " + resultActions.andReturn().getResponse().getContentAsString());

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("board1 after change"))
                .andExpect(jsonPath("$.data[0].type").value(BoardType.BOARD.getCode()))

                .andExpect(jsonPath("$.data[1].name").value("board2 after change"))
                .andExpect(jsonPath("$.data[1].type").value(BoardType.BOARD.getCode()))

                .andExpect(jsonPath("$.data[2].name").value("board3 after change"))
                .andExpect(jsonPath("$.data[2].type").value(BoardType.BOARD.getCode()))
                .andDo(document("board-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].id").description("게시판 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("[].name").description("게시판 이름").type(JsonFieldType.STRING),
                                fieldWithPath("[].isAdminOnly").description("관리자만 글을 쓸 수 있는지 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("[].orderIndex").description("게시판 순서").type(JsonFieldType.NUMBER),
                                fieldWithPath("[].isDeleted").description("삭제 여부").type(JsonFieldType.BOOLEAN).optional(),
                                fieldWithPath("[].category").description("하위 게시판 정보").type(JsonFieldType.ARRAY).optional(),
                                fieldWithPath("[].category[].id").description("하위 게시판 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("[].category[].name").description("하위 게시판 이름").type(JsonFieldType.STRING),
                                fieldWithPath("[].category[].orderIndex").description("하위 게시판 순서").type(JsonFieldType.NUMBER),
                                fieldWithPath("[].category[].isAdminOnly").description("관리자만 글을 쓸 수 있는지 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("[].category[].isDeleted").description("삭제 여부").type(JsonFieldType.BOOLEAN).optional()
                        ),
                        responseFields(
                                fieldWithPath("data[].id").description("게시판 고유 식별 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].name").description("게시판 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data[].isAdminOnly").description("관리자만 글을 쓸 수 있는지 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data[].orderIndex").description("게시판 순서").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].isDeleted").description("삭제 여부").type(JsonFieldType.BOOLEAN).optional(),
                                fieldWithPath("data[].type").description("게시판 유형").type(JsonFieldType.STRING),

                                fieldWithPath("data[].category").description("하위 게시판 정보").type(JsonFieldType.ARRAY).optional(),
                                fieldWithPath("data[].category[].id").description("하위 게시판 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].category[].name").description("하위 게시판 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data[].category[].orderIndex").description("하위 게시판 순서").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].category[].type").description("게시판 유형").type(JsonFieldType.STRING),
                                fieldWithPath("data[].category[].parentId").description("상위 게시판의 고유 식별 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].category[].isAdminOnly").description("관리자만 글을 쓸 수 있는지 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data[].category[].isDeleted").description("삭제 여부").type(JsonFieldType.BOOLEAN).optional()

                        ).andWithPrefix("", pageNoContentResponseFields())

                ));
    }
}