package com.cpayusin.board.controller.port;

import com.cpayusin.board.controller.request.BoardCreateRequest;
import com.cpayusin.board.controller.request.BoardUpdateRequest;
import com.cpayusin.board.controller.request.CategoryUpdateRequest;
import com.cpayusin.board.controller.response.BoardCreateResponse;
import com.cpayusin.board.controller.response.BoardMenuResponse;
import com.cpayusin.board.controller.response.BoardResponse;
import com.cpayusin.board.domain.BoardDomain;
import com.cpayusin.board.infrastructure.Board;
import com.cpayusin.member.domain.MemberDomain;
import com.cpayusin.member.infrastructure.Member;

import java.util.List;

public interface BoardService
{
    BoardCreateResponse createBoard(BoardCreateRequest request, MemberDomain currentMember);

    void bulkUpdateBoards(List<BoardUpdateRequest> requests, MemberDomain currentMember);


    BoardDomain getBoardById(Long boardId);

    BoardResponse findBoardById(Long boardId);

    List<BoardResponse> findCategoryByBoardId(Long boardId);

    List<BoardMenuResponse> getMenuList();

    List<Long> getBoardIdListByParentId(Long boardId);
}
