package com.cpayusin.board.service.port;

import com.cpayusin.board.infrastructure.BoardEntity;

import java.util.List;
import java.util.Optional;

public interface BoardRepository
{
    List<BoardEntity> findBoardByParentBoardId(Long parentId);

    Integer countChildrenByParentId(Long parentId);

    Integer countParent();

    List<Long> findBoardIdListByParentId(Long boardId);

    BoardEntity save(BoardEntity board);

    Optional<BoardEntity> findById(Long boardId);

    List<BoardEntity> findAll();

    void deleteById(Long boardId);
}
