package com.cpayusin.board.infrastructure;

import com.cpayusin.board.domain.BoardDomain;
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
    public List<BoardDomain> findBoardByParentBoardId(Long parentId)
    {
        return boardJpaRepository.findBoardByParentBoardId(parentId).stream()
                .map(Board::toModel)
                .toList();
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
    public BoardDomain save(BoardDomain board)
    {
        return boardJpaRepository.save(Board.from(board))
                .toModel();
    }

    @Override
    public Optional<BoardDomain> findById(Long boardId)
    {
        return boardJpaRepository.findById(boardId)
                .map(Board::toModel);
    }

    @Override
    public List<BoardDomain> findAll()
    {
        return boardJpaRepository.findAll().stream()
                .map(Board::toModel)
                .toList();
    }

    @Override
    public void deleteById(Long boardId)
    {
        boardJpaRepository.deleteById(boardId);
    }

    @Override
    public List<BoardDomain> saveAll(List<BoardDomain> domains)
    {
        List<Board> boards = domains.stream()
                .map(Board::from)
                .toList();

        return boardJpaRepository.saveAll(boards).stream()
                .map(Board::toModel)
                .toList();
    }
}
