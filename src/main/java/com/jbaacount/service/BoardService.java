package com.jbaacount.service;

import com.jbaacount.model.Board;
import com.jbaacount.model.Member;
import com.jbaacount.payload.request.board.BoardCreateRequest;
import com.jbaacount.payload.request.board.BoardUpdateRequest;
import com.jbaacount.payload.request.board.CategoryUpdateRequest;
import com.jbaacount.payload.response.board.BoardCreateResponse;
import com.jbaacount.payload.response.board.BoardMenuResponse;
import com.jbaacount.payload.response.board.BoardResponse;

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
