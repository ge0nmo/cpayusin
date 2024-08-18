package com.cpayusin.integration;

import com.cpayusin.board.domain.type.BoardType;
import com.cpayusin.board.domain.Board;
import com.cpayusin.board.service.port.BoardRepository;
import com.cpayusin.config.TearDownExtension;
import com.cpayusin.config.TestContainerExtension;
import com.cpayusin.dummy.DummyObject;
import com.cpayusin.member.domain.Member;
import com.cpayusin.member.service.port.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ExtendWith(TearDownExtension.class)
@ExtendWith(TestContainerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class BoardTest extends DummyObject
{
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp()
    {
        Member member = newMockMember(1L, "abc@naver.com", "mockUser", "ADMIN");
        memberRepository.save(member);

        Board board1 = boardRepository.save(newMockBoard(1L, "board1", BoardType.BOARD.name(),1));

        boardRepository.save(newMockBoard(2L, "board2", BoardType.BOARD.name(),2));

        Board category1 = newMockBoard(3L, "category1", BoardType.CATEGORY.name(),1);
        Board category2 = newMockBoard(4L, "category2", BoardType.CATEGORY.name(), 2);

        category1.addParent(board1);
        category2.addParent(board1);

        boardRepository.save(category1);
        boardRepository.save(category2);
    }

    @Test
    void getMenu() throws Exception
    {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/board/menu"));

        String responseBody = objectMapper.writeValueAsString(resultActions.andReturn().getResponse().getContentAsString());

        System.out.println("response body = " + responseBody);
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("board1"))
                .andExpect(jsonPath("$.data[0].type").value(BoardType.BOARD.name()))
                .andExpect(jsonPath("$.data[0].category[0].id").value(3L))
                .andExpect(jsonPath("$.data[0].category[0].name").value("category1"))
                .andExpect(jsonPath("$.data[0].category[0].type").value(BoardType.CATEGORY.name()))
                .andExpect(jsonPath("$.data[0].category[1].id").value(4L))
                .andExpect(jsonPath("$.data[0].category[1].name").value("category2"))
                .andExpect(jsonPath("$.data[0].category[1].type").value(BoardType.CATEGORY.name()))


                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].name").value("board2"))
                .andExpect(jsonPath("$.data[1].type").value(BoardType.BOARD.name()));

    }

    @Test
    void getBoardById() throws Exception
    {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/board/single-info/" + 1));

        String responseBody = objectMapper.writeValueAsString(resultActions.andReturn().getResponse().getContentAsString());

        System.out.println("response body = " + responseBody);
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("board1"));
    }

    @Test
    void getCategoryList() throws Exception
    {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/board/category/" + 1));

        String responseBody = objectMapper.writeValueAsString(resultActions.andReturn().getResponse().getContentAsString());

        System.out.println("response body = " + responseBody);
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(3L))
                .andExpect(jsonPath("$.data[0].name").value("category1"))
                .andExpect(jsonPath("$.data[0].orderIndex").value(1))

                .andExpect(jsonPath("$.data[1].id").value(4L))
                .andExpect(jsonPath("$.data[1].name").value("category2"))
                .andExpect(jsonPath("$.data[1].orderIndex").value(2));
    }
}