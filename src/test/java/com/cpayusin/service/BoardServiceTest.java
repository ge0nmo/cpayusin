package com.cpayusin.service;

import com.cpayusin.board.controller.request.BoardCreateRequest;
import com.cpayusin.board.controller.response.BoardCreateResponse;
import com.cpayusin.board.controller.response.BoardResponse;
import com.cpayusin.board.domain.Board;
import com.cpayusin.board.domain.type.BoardType;
import com.cpayusin.board.service.BoardServiceImpl;
import com.cpayusin.board.service.port.BoardRepository;
import com.cpayusin.common.exception.AuthenticationException;
import com.cpayusin.common.service.UtilService;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.service.PostServiceImpl;
import com.cpayusin.setup.MockSetup;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest extends MockSetup
{
    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private PostServiceImpl postService;

    @Spy
    private UtilService utilService;

    @Spy
    private ObjectMapper om;


    @DisplayName("게시판 생성")
    @Test
    void createBoard_Admin() throws JsonProcessingException
    {
        // given
        Long boardId = 1L;
        String name = "게시판";
        BoardCreateRequest request = new BoardCreateRequest();
        request.setName(name);
        request.setIsAdminOnly(true);

        // stub 1
        Member admin = newMockMember(1L, "aaa@naver.com", "admin", "ADMIN");
        Member user = newMockMember(1L, "aaa@naver.com", "admin", "USER");
        Board board = newMockBoard(boardId, name, BoardType.BOARD.name(),1);

        when(boardRepository.save(any())).thenReturn(board);
        utilService.isAdmin(admin);
        assertThrows(AuthenticationException.class, () -> utilService.isAdmin(user));

        // when
        BoardCreateResponse response = boardService.createBoard(request, admin);
        String responseBody = om.writeValueAsString(response);

        System.out.println("response body = " + responseBody);

        // then
        assertThat(response.getName()).isEqualTo("게시판");

    }

    @Test
    void findBoardById()
    {
        // given
        given(boardRepository.findById(any(Long.class))).willReturn(Optional.of(mockBoard1));

        // when
        BoardResponse response = boardService.findBoardById(1L);

        // then
        assertThat(response.getOrderIndex()).isEqualTo(mockBoard1.getOrderIndex());
        assertThat(response.getName()).isEqualTo(mockBoard1.getName());
        verify(boardRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void getBoardIdListByParentId()
    {
        // given
        mockBoard2.setParent(mockBoard1);
        mockBoard3.setParent(mockBoard1);

        List<Long> childrenList = new ArrayList<>();
        childrenList.add(mockBoard2.getId());
        childrenList.add(mockBoard3.getId());

        given(boardRepository.findBoardIdListByParentId(any())).willReturn(childrenList);

        // when
        List<Long> response = boardService.getBoardIdListByParentId(mockBoard1.getId());

        // then
        assertThat(response.size()).isEqualTo(2);
        verify(boardRepository, times(1)).findBoardIdListByParentId(any());

    }
}

