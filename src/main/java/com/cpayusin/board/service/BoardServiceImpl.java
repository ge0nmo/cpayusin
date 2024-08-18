package com.cpayusin.board.service;

import com.cpayusin.board.controller.request.CategoryUpdateRequest;
import com.cpayusin.board.controller.response.BoardChildrenResponse;
import com.cpayusin.board.controller.response.BoardCreateResponse;
import com.cpayusin.board.controller.response.BoardMenuResponse;
import com.cpayusin.board.controller.response.BoardResponse;
import com.cpayusin.board.domain.Board;
import com.cpayusin.board.domain.type.BoardType;
import com.cpayusin.board.service.port.BoardRepository;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.board.mapper.BoardMapper;
import com.cpayusin.member.domain.Member;
import com.cpayusin.board.controller.request.BoardCreateRequest;
import com.cpayusin.board.controller.request.BoardUpdateRequest;
import com.cpayusin.board.controller.port.BoardService;
import com.cpayusin.post.controller.port.PostService;
import com.cpayusin.common.service.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class BoardServiceImpl implements BoardService
{
    private final BoardRepository boardRepository;
    private final UtilService utilService;
    private final PostService postService;

    @Transactional
    @Override
    public BoardCreateResponse createBoard(BoardCreateRequest request, Member currentMember)
    {
        utilService.isAdmin(currentMember);
        Board board;

        if (request.getParentId() != null) {
            Board parent = getBoardById(request.getParentId());

            if (parent.getParent() != null)
                throw new BusinessLogicException(ExceptionMessage.BOARD_TYPE_ERROR);

            int orderIndex = boardRepository.countChildrenByParentId(parent.getId()) + 1;
            board = BoardMapper.INSTANCE.toBoardEntity(request, orderIndex, BoardType.CATEGORY.name());
            board.addParent(parent);

        } else {
            int orderIndex = boardRepository.countParent() + 1;

            board = BoardMapper.INSTANCE.toBoardEntity(request, orderIndex, BoardType.BOARD.name());
        }
        return BoardMapper.INSTANCE.toBoardCreateResponse(boardRepository.save(board));
    }


    @Transactional
    @Override
    public List<BoardMenuResponse> bulkUpdateBoards(List<BoardUpdateRequest> requests, Member currentMember)
    {
        utilService.isAdmin(currentMember);
        List<BoardUpdateRequest> removedBoardList = requests.stream()
                .filter(board -> board.getIsDeleted() != null && board.getIsDeleted())
                .toList();
        List<BoardUpdateRequest> updateBoardList = requests.stream()
                .filter(board -> board.getIsDeleted() == null || !board.getIsDeleted())
                .toList();

        updateBoardList
                .forEach(request -> {
                    Board board = getBoardById(request.getId());
                    BoardMapper.INSTANCE.updateBoard(request, board);
                    board.setParent(null);
                    board.setType(BoardType.BOARD.name());

                    if (!request.getCategory().isEmpty())
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


    private void updateCategory(Board parent, List<CategoryUpdateRequest> requests)
    {
        requests.forEach(
                request -> {
                    Board category = getBoardById(request.getId());
                    if (request.getIsDeleted() != null && request.getIsDeleted())
                        deleteBoard(request.getId());
                    else {
                        BoardMapper.INSTANCE.updateBoard(request, category);
                        category.setType(BoardType.CATEGORY.name());
                        category.addParent(parent);
                    }
                }
        );
    }

    @Override
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

    @Override
    public List<BoardMenuResponse> getMenuList()
    {
        List<Board> result = boardRepository.findAll();
        if (result.isEmpty())
            return Collections.emptyList();

        log.info("result = {}", result);

        List<BoardMenuResponse> boardList = result.stream()
                .filter(board -> board.getType().equals(BoardType.BOARD.name()))
                .sorted(Comparator.comparingInt(Board::getOrderIndex))
                .map(BoardMenuResponse::toBoardResponse)
                .collect(toList());

        log.info("boardList = {}", boardList);

        Map<Long, List<BoardChildrenResponse>> categoryMap = result.stream()
                .filter(b -> b.getType().equals(BoardType.CATEGORY.name()))
                .collect(groupingBy(
                        b -> b.getParent().getId(),
                        Collectors.mapping(
                                BoardChildrenResponse::toCategoryResponse,
                                toList()
                        )));

        for(BoardMenuResponse response : boardList)
        {
            response.setCategory(categoryMap.getOrDefault(response.getId(), new ArrayList<>()));
        }

        return boardList;
    }



    @Override
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
        if (!childrenList.isEmpty())
            childrenList.forEach(childBoard -> {
                deleteBoard(childBoard.getId());
            });
    }

}
