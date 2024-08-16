package com.cpayusin.board.service.port;

import com.cpayusin.board.domain.BoardDomain;
import com.cpayusin.board.infrastructure.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository
{
    List<BoardDomain> findBoardByParentBoardId(Long parentId);

    Integer countChildrenByParentId(Long parentId);

    Integer countParent();

    List<Long> findBoardIdListByParentId(Long boardId);

    BoardDomain save(BoardDomain board);

    List<BoardDomain> saveAll(List<BoardDomain> board);

    Optional<BoardDomain> findById(Long boardId);

    List<BoardDomain> findAll();

    void deleteById(Long boardId);
}
