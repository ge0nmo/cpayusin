package com.cpayusin.board.service;

import com.cpayusin.board.controller.request.CategoryUpdateRequest;
import com.cpayusin.board.controller.response.BoardChildrenResponse;
import com.cpayusin.board.controller.response.BoardCreateResponse;
import com.cpayusin.board.controller.response.BoardMenuResponse;
import com.cpayusin.board.controller.response.BoardResponse;
import com.cpayusin.board.infrastructure.BoardEntity;
import com.cpayusin.board.domain.type.BoardType;
import com.cpayusin.board.service.port.BoardRepository;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.mapper.BoardMapper;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.board.controller.request.BoardCreateRequest;
import com.cpayusin.board.controller.request.BoardUpdateRequest;
import com.cpayusin.board.controller.port.BoardService;
import com.cpayusin.post.controller.port.PostService;
import com.cpayusin.common.service.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    public BoardCreateResponse createBoard(BoardCreateRequest request, MemberEntity currentMemberEntity)
    {
        utilService.isAdmin(currentMemberEntity);
        BoardEntity board = BoardMapper.INSTANCE.toBoardEntity(request);
        if (request.getParentId() != null) {
            BoardEntity parent = getBoardById(request.getParentId());
            if (parent.getParent() != null)
                throw new BusinessLogicException(ExceptionMessage.BOARD_TYPE_ERROR);
            board.addParent(parent);
            Integer orderIndex = boardRepository.countChildrenByParentId(parent.getId());
            board.updateOrderIndex(orderIndex + 1);
            board.updateBoardType(BoardType.CATEGORY.getCode());
        } else {
            Integer orderIndex = boardRepository.countParent();
            board.updateOrderIndex(orderIndex + 1);
        }
        return BoardMapper.INSTANCE.toBoardCreateResponse(boardRepository.save(board));
    }


    @Transactional
    public List<BoardMenuResponse> bulkUpdateBoards(List<BoardUpdateRequest> requests, MemberEntity currentMemberEntity)
    {
        utilService.isAdmin(currentMemberEntity);
        List<BoardUpdateRequest> removedBoardList = requests.stream()
                .filter(board -> board.getIsDeleted() != null && board.getIsDeleted())
                .toList();
        List<BoardUpdateRequest> updateBoardList = requests.stream()
                .filter(board -> board.getIsDeleted() == null || !board.getIsDeleted())
                .toList();
        updateBoardList
                .forEach(request -> {
                    BoardEntity board = getBoardById(request.getId());
                    BoardMapper.INSTANCE.updateBoard(request, board);
                    board.setParent(null);
                    board.setType(BoardType.BOARD.getCode());
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

    public void updateCategory(BoardEntity parent, List<CategoryUpdateRequest> requests)
    {
        requests.forEach(
                request -> {
                    BoardEntity category = getBoardById(request.getId());
                    if (request.getIsDeleted() != null && request.getIsDeleted())
                        deleteBoard(request.getId());
                    else {
                        BoardMapper.INSTANCE.updateBoard(request, category);
                        category.setType(BoardType.CATEGORY.getCode());
                        category.addParent(parent);
                    }
                }
        );
    }

    public BoardEntity getBoardById(Long boardId)
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
        List<BoardEntity> result = boardRepository.findAll();
        if (result.isEmpty())
            return Collections.emptyList();
        List<BoardMenuResponse> boardList = BoardMapper.INSTANCE.toBoardMenuResponse(result.stream()
                .filter(board -> board.getType().equals(BoardType.BOARD.getCode()))
                .sorted(Comparator.comparingInt(BoardEntity::getOrderIndex))
                .collect(toList()));
        List<BoardChildrenResponse> categoryList = BoardMapper.INSTANCE.toChildrenList(result.stream()
                .filter(board -> board.getType().equals(BoardType.CATEGORY.getCode()))
                .sorted(Comparator.comparingInt(BoardEntity::getOrderIndex))
                .collect(toList()));
        boardList.forEach(board -> board.setCategory(
                categoryList.stream()
                        .filter(category -> category.getParentId().equals(board.getId()))
                        .collect(toList())));
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
        List<BoardEntity> childrenList = boardRepository.findBoardByParentBoardId(boardId);
        if (!childrenList.isEmpty())
            childrenList.forEach(childBoard -> {
                deleteBoard(childBoard.getId());
            });
    }

}
