package com.cpayusin.board.controller.port;

import com.cpayusin.board.controller.request.BoardCreateRequest;
import com.cpayusin.board.controller.request.BoardUpdateRequest;
import com.cpayusin.board.controller.request.CategoryUpdateRequest;
import com.cpayusin.board.controller.response.BoardCreateResponse;
import com.cpayusin.board.controller.response.BoardMenuResponse;
import com.cpayusin.board.controller.response.BoardResponse;
import com.cpayusin.board.domain.Board;
import com.cpayusin.member.domain.Member;

import java.util.List;

public interface BoardService
{
    BoardCreateResponse createBoard(BoardCreateRequest request, Member currentMember);

    List<BoardMenuResponse> bulkUpdateBoards(List<BoardUpdateRequest> requests, Member currentMember);

    void updateCategory(Board parent, List<CategoryUpdateRequest> requests);

    Board getBoardById(Long boardId);

    BoardResponse findBoardById(Long boardId);

    List<BoardResponse> findCategoryByBoardId(Long boardId);

    List<BoardMenuResponse> getMenuList();

    List<Long> getBoardIdListByParentId(Long boardId);
}
