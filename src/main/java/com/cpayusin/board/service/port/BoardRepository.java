package com.cpayusin.board.service.port;

import com.cpayusin.board.infrastructure.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository
{
    List<Board> findBoardByParentBoardId(Long parentId);

    Integer countChildrenByParentId(Long parentId);

    Integer countParent();

    List<Long> findBoardIdListByParentId(Long boardId);

    Board save(Board board);

    Optional<Board> findById(Long boardId);

    List<Board> findAll();

    void deleteById(Long boardId);
}
