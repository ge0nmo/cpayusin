package com.jbaacount.service;

import com.jbaacount.global.exception.BusinessLogicException;
import com.jbaacount.global.exception.ExceptionMessage;
import com.jbaacount.mapper.BoardMapper;
import com.jbaacount.model.Board;
import com.jbaacount.model.Member;
import com.jbaacount.model.type.BoardType;
import com.jbaacount.payload.request.board.BoardCreateRequest;
import com.jbaacount.payload.request.board.BoardUpdateRequest;
import com.jbaacount.payload.request.board.CategoryUpdateRequest;
import com.jbaacount.payload.response.board.BoardChildrenResponse;
import com.jbaacount.payload.response.board.BoardCreateResponse;
import com.jbaacount.payload.response.board.BoardMenuResponse;
import com.jbaacount.payload.response.board.BoardResponse;
import com.jbaacount.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class BoardService
{
    private final BoardRepository boardRepository;
    private final UtilService utilService;
    private final PostService postService;

    @Transactional
    public BoardCreateResponse createBoard(BoardCreateRequest request, Member currentMember)
    {
        utilService.isAdmin(currentMember);

        Board board = BoardMapper.INSTANCE.toBoardEntity(request);
        if (request.getParentId() != null)
        {
            Board parent = getBoardById(request.getParentId());
            if (parent.getParent() != null)
                throw new BusinessLogicException(ExceptionMessage.BOARD_TYPE_ERROR);

            board.addParent(parent);
            Integer orderIndex = boardRepository.countChildrenByParentId(parent.getId());
            board.updateOrderIndex(orderIndex + 1);
            board.updateBoardType(BoardType.CATEGORY.getCode());
        }
        else
        {
            Integer orderIndex = boardRepository.countParent();
            board.updateOrderIndex(orderIndex + 1);
        }

        return BoardMapper.INSTANCE.toBoardCreateResponse(boardRepository.save(board));
    }


    @Transactional
    public List<BoardMenuResponse> bulkUpdateBoards(List<BoardUpdateRequest> requests, Member currentMember)
    {
        utilService.isAdmin(currentMember);

        List<BoardUpdateRequest> removedBoardList = requests.stream()
                .filter(board -> board.getIsDeleted() != null && board.getIsDeleted())
                .collect(Collectors.toList());

        List<BoardUpdateRequest> updateBoardList = requests.stream()
                .filter(board -> board.getIsDeleted() == null || !board.getIsDeleted())
                .collect(Collectors.toList());

        updateBoardList
                .forEach(request -> {
                    Board board = getBoardById(request.getId());

                    BoardMapper.INSTANCE.updateBoard(request, board);
                    board.setParent(null);
                    board.setType(BoardType.BOARD.getCode());

                    if(!request.getCategory().isEmpty())
                        updateCategory(board, request.getCategory());
                });

        removedBoardList
                .forEach(request -> {
                    getBoardById(request.getId());
                    removeAllChildrenBoard(request.getId());
                    deleteBoard(request.getId());
                });


        return getMenuList();
    }

    public void updateCategory(Board parent, List<CategoryUpdateRequest> requests)
    {
        requests.forEach(
                request -> {
                    Board category = getBoardById(request.getId());

                    if(request.getIsDeleted() != null && request.getIsDeleted())
                        deleteBoard(request.getId());

                    else{
                        BoardMapper.INSTANCE.updateBoard(request, category);
                        category.setType(BoardType.CATEGORY.getCode());

                        category.addParent(parent);
                    }
                }
        );
    }

    public Board getBoardById(Long boardId)
    {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.BOARD_NOT_FOUND));
    }

    public BoardResponse findBoardById(Long boardId)
    {
        return BoardMapper.INSTANCE.boardToResponse(getBoardById(boardId));
    }

    public List<BoardResponse> findCategoryByBoardId(Long boardId)
    {
        var result = boardRepository.findBoardByParentBoardId(boardId);

        return BoardMapper.INSTANCE.toBoardResponseList(result);
    }

    public List<BoardMenuResponse> getMenuList()
    {
        List<Board> result = boardRepository.findAll();

        if(result.isEmpty())
            return Collections.emptyList();

        List<BoardMenuResponse> boardList = BoardMapper.INSTANCE.toBoardMenuResponse(result.stream()
                .filter(board -> board.getType().equals(BoardType.BOARD.getCode()))
                .sorted(Comparator.comparingInt(Board::getOrderIndex))
                .collect(Collectors.toList()));

        List<BoardChildrenResponse> categoryList = BoardMapper.INSTANCE.toChildrenList(result.stream()
                .filter(board -> board.getType().equals(BoardType.CATEGORY.getCode()))
                .sorted(Comparator.comparingInt(Board::getOrderIndex))
                .collect(Collectors.toList()));


        boardList.forEach(board -> board.setCategory(
                categoryList.stream()
                        .filter(category -> category.getParentId().equals(board.getId()))
                        .collect(Collectors.toList())));

        return boardList;
    }

    public List<Long> getBoardIdListByParentId(Long boardId)
    {
        return boardRepository.findBoardIdListByParentId(boardId);
    }


    private void deleteBoard(Long boardId)
    {
        postService.deleteAllPostsByBoardId(boardId);

        boardRepository.deleteById(boardId);
    }

    private void removeAllChildrenBoard(Long boardId)
    {
        List<Board> childrenList = boardRepository.findBoardByParentBoardId(boardId);

        if(!childrenList.isEmpty())
            childrenList.forEach(childBoard -> {
                deleteBoard(childBoard.getId());
            });
    }

}
