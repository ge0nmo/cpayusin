package com.cpayusin.integration;

import com.cpayusin.board.controller.request.BoardCreateRequest;
import com.cpayusin.board.controller.request.BoardUpdateRequest;
import com.cpayusin.board.controller.request.CategoryUpdateRequest;
import com.cpayusin.board.domain.type.BoardType;
import com.cpayusin.board.domain.Board;
import com.cpayusin.board.service.port.BoardRepository;
import com.cpayusin.config.TearDownExtension;
import com.cpayusin.config.TestContainerExtension;
import com.cpayusin.dummy.DummyObject;
import com.cpayusin.member.domain.Member;
import com.cpayusin.member.service.port.MemberRepository;
import com.cpayusin.post.domain.Post;
import com.cpayusin.post.service.port.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(TearDownExtension.class)
@ExtendWith(TestContainerExtension.class)
class AdminTest extends DummyObject
{
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;


    @Autowired
    private PostRepository postRepository;

    Member member;
    Board board1;
    Board board2;
    Board childBoard1;

    @BeforeEach
    void setUp()
    {
        member = newMockMember(1L, "aa@naver.com", "test1", "ADMIN");
        memberRepository.save(member);

        board1 = newMockBoard(1L, "board1", BoardType.BOARD.name(),1);
        board2 = newMockBoard(2L, "board2", BoardType.BOARD.name(),2);

        childBoard1 = newMockBoard(3L, "child boardEntity", BoardType.CATEGORY.name(), 1);
        childBoard1.setParent(board1);

        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(childBoard1);
    }


    @WithUserDetails(value = "aa@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void saveBoard_test() throws Exception
    {
        // given
        BoardCreateRequest request = new BoardCreateRequest();
        request.setIsAdminOnly(false);
        request.setName("mockBoard1");

        String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/admin/manage/board/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                );


        // then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response body = " + responseBody);

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("mockBoard1"));
    }

    @WithUserDetails(value = "aa@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateBoard_test1() throws Exception
    {
        // given
        BoardUpdateRequest boardRequest1 = new BoardUpdateRequest();
        boardRequest1.setId(1L);
        boardRequest1.setName("board1 after change");
        boardRequest1.setOrderIndex(1);


        BoardUpdateRequest boardRequest2 = new BoardUpdateRequest();
        boardRequest2.setId(2L);
        boardRequest2.setName("board2 after change");
        boardRequest2.setOrderIndex(2);

        BoardUpdateRequest boardRequest3 = new BoardUpdateRequest();
        boardRequest3.setId(3L);
        boardRequest3.setName("board3 after change");
        boardRequest3.setOrderIndex(3);

        List<BoardUpdateRequest> boardUpdateRequestList = List.of(boardRequest1, boardRequest2, boardRequest3);

        String requestBody = objectMapper.writeValueAsString(boardUpdateRequestList);
        // when

        ResultActions resultActions = mvc
                .perform(patch("/api/v1/admin/manage/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        System.out.println("response body = " + resultActions.andReturn().getResponse().getContentAsString());

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("board1 after change"))
                .andExpect(jsonPath("$.data[0].type").value(BoardType.BOARD.name()))

                .andExpect(jsonPath("$.data[1].name").value("board2 after change"))
                .andExpect(jsonPath("$.data[1].type").value(BoardType.BOARD.name()))

                .andExpect(jsonPath("$.data[2].name").value("board3 after change"))
                .andExpect(jsonPath("$.data[2].type").value(BoardType.BOARD.name()));

    }

    @WithUserDetails(value = "aa@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateBoard_test2() throws Exception
    {
        // given

        // boardEntity 1
        BoardUpdateRequest boardRequest1 = new BoardUpdateRequest();
        boardRequest1.setId(1L);
        boardRequest1.setName("board1 after change");
        boardRequest1.setOrderIndex(1);

        // category 1
        CategoryUpdateRequest categoryUpdateRequest1 = new CategoryUpdateRequest();
        categoryUpdateRequest1.setId(2L);
        categoryUpdateRequest1.setName("board1 in board1");
        categoryUpdateRequest1.setOrderIndex(1);

        // category 2
        CategoryUpdateRequest categoryUpdateRequest2 = new CategoryUpdateRequest();
        categoryUpdateRequest2.setId(3L);
        categoryUpdateRequest2.setName("board2 in board1");
        categoryUpdateRequest2.setOrderIndex(2);

        List<CategoryUpdateRequest> board1CategoryList = new ArrayList<>();
        board1CategoryList.add(categoryUpdateRequest1);
        board1CategoryList.add(categoryUpdateRequest2);

        boardRequest1.setCategory(board1CategoryList);

        List<BoardUpdateRequest> boardUpdateRequestList = new ArrayList<>();
        boardUpdateRequestList.add(boardRequest1);

        String requestBody = objectMapper.writeValueAsString(boardUpdateRequestList);
        // when

        ResultActions resultActions = mvc
                .perform(patch("/api/v1/admin/manage/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        System.out.println("response body = " + resultActions.andReturn().getResponse().getContentAsString());

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("board1 after change"))
                .andExpect(jsonPath("$.data[0].type").value(BoardType.BOARD.name()))

                .andExpect(jsonPath("$.data[0].category[0].name").value("board1 in board1"))
                .andExpect(jsonPath("$.data[0].category[0].type").value(BoardType.CATEGORY.name()))

                .andExpect(jsonPath("$.data[0].category[1].name").value("board2 in board1"))
                .andExpect(jsonPath("$.data[0].category[1].type").value(BoardType.CATEGORY.name()));

    }


    @WithUserDetails(value = "aa@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateBoard_test3() throws Exception
    {
        // given
        BoardUpdateRequest boardRequest1 = new BoardUpdateRequest();
        boardRequest1.setId(1L);
        boardRequest1.setName("board1 after change");
        boardRequest1.setOrderIndex(1);
        boardRequest1.setIsDeleted(true);


        BoardUpdateRequest boardRequest2 = new BoardUpdateRequest();
        boardRequest2.setId(2L);
        boardRequest2.setName("board2 after change");
        boardRequest2.setOrderIndex(2);
        boardRequest2.setIsDeleted(true);

        BoardUpdateRequest boardRequest3 = new BoardUpdateRequest();
        boardRequest3.setId(3L);
        boardRequest3.setName("board3 after change");
        boardRequest3.setOrderIndex(3);
        boardRequest3.setIsDeleted(false);

        List<BoardUpdateRequest> boardUpdateRequestList = List.of(boardRequest1, boardRequest2, boardRequest3);

        String requestBody = objectMapper.writeValueAsString(boardUpdateRequestList);

        // when

        ResultActions resultActions = mvc
                .perform(patch("/api/v1/admin/manage/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        System.out.println("response body = " + resultActions.andReturn().getResponse().getContentAsString());

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("board3 after change"))
                .andExpect(jsonPath("$.data[0].type").value(BoardType.BOARD.name()));
    }

    @DisplayName("Test if posts are also removed")
    @WithUserDetails(value = "aa@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateBoard_test4() throws Exception
    {
        // given
        Post post1 = newMockPost(1L, "test", "content", board1, member);
        Post post2 = newMockPost(1L, "test", "content", board2, member);
        postRepository.save(post1);
        postRepository.save(post2);

        BoardUpdateRequest boardRequest1 = new BoardUpdateRequest();
        boardRequest1.setId(1L);
        boardRequest1.setName("board1 after change");
        boardRequest1.setOrderIndex(1);
        boardRequest1.setIsDeleted(true);

        BoardUpdateRequest boardRequest2 = new BoardUpdateRequest();
        boardRequest2.setId(2L);
        boardRequest2.setName("board2 after change");
        boardRequest2.setOrderIndex(2);
        boardRequest2.setIsDeleted(true);


        List<BoardUpdateRequest> boardUpdateRequestList = List.of(boardRequest1, boardRequest2);

        String requestBody = objectMapper.writeValueAsString(boardUpdateRequestList);

        // when
        ResultActions resultActions = mvc
                .perform(patch("/api/v1/admin/manage/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        System.out.println("response body = " + resultActions.andReturn().getResponse().getContentAsString());

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("데이터가 없습니다."));
    }
}