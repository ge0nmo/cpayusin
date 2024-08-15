package com.cpayusin.board.controller.port;

import com.cpayusin.board.controller.request.BoardCreateRequest;
import com.cpayusin.board.controller.request.BoardUpdateRequest;
import com.cpayusin.board.controller.request.CategoryUpdateRequest;
import com.cpayusin.board.controller.response.BoardCreateResponse;
import com.cpayusin.board.controller.response.BoardMenuResponse;
import com.cpayusin.board.controller.response.BoardResponse;
import com.cpayusin.board.infrastructure.BoardEntity;
import com.cpayusin.member.infrastructure.MemberEntity;

import java.util.List;

public interface BoardService
{
    BoardCreateResponse createBoard(BoardCreateRequest request, MemberEntity currentMemberEntity);

    List<BoardMenuResponse> bulkUpdateBoards(List<BoardUpdateRequest> requests, MemberEntity currentMemberEntity);

    void updateCategory(BoardEntity parent, List<CategoryUpdateRequest> requests);

    BoardEntity getBoardById(Long boardId);

    BoardResponse findBoardById(Long boardId);

    List<BoardResponse> findCategoryByBoardId(Long boardId);

    List<BoardMenuResponse> getMenuList();

    List<Long> getBoardIdListByParentId(Long boardId);
}
