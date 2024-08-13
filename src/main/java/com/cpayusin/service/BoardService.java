package com.cpayusin.service;

import com.cpayusin.model.Board;
import com.cpayusin.model.Member;
import com.cpayusin.payload.request.board.BoardCreateRequest;
import com.cpayusin.payload.request.board.BoardUpdateRequest;
import com.cpayusin.payload.request.board.CategoryUpdateRequest;
import com.cpayusin.payload.response.board.BoardCreateResponse;
import com.cpayusin.payload.response.board.BoardMenuResponse;
import com.cpayusin.payload.response.board.BoardResponse;

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
