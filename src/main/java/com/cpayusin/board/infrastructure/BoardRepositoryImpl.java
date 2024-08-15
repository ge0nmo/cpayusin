package com.cpayusin.board.infrastructure;

import com.cpayusin.board.service.port.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class BoardRepositoryImpl implements BoardRepository
{
    private final BoardJpaRepository boardJpaRepository;

    @Override
    public List<BoardEntity> findBoardByParentBoardId(Long parentId)
    {
        return boardJpaRepository.findBoardByParentBoardId(parentId);
    }

    @Override
    public Integer countChildrenByParentId(Long parentId)
    {
        return boardJpaRepository.countChildrenByParentId(parentId);
    }

    @Override
    public Integer countParent()
    {
        return boardJpaRepository.countParent();
    }

    @Override
    public List<Long> findBoardIdListByParentId(Long boardId)
    {
        return boardJpaRepository.findBoardIdListByParentId(boardId);
    }

    @Override
    public BoardEntity save(BoardEntity board)
    {
        return boardJpaRepository.save(board);
    }

    @Override
    public Optional<BoardEntity> findById(Long boardId)
    {
        return boardJpaRepository.findById(boardId);
    }

    @Override
    public List<BoardEntity> findAll()
    {
        return boardJpaRepository.findAll();
    }

    @Override
    public void deleteById(Long boardId)
    {
        boardJpaRepository.deleteById(boardId);
    }
}
