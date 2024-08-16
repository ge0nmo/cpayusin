package com.cpayusin.board.service;

import com.cpayusin.board.controller.request.CategoryUpdateRequest;
import com.cpayusin.board.controller.response.BoardChildrenResponse;
import com.cpayusin.board.controller.response.BoardCreateResponse;
import com.cpayusin.board.controller.response.BoardMenuResponse;
import com.cpayusin.board.controller.response.BoardResponse;
import com.cpayusin.board.domain.BoardDomain;
import com.cpayusin.board.infrastructure.Board;
import com.cpayusin.board.domain.type.BoardType;
import com.cpayusin.board.service.port.BoardRepository;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.mapper.BoardMapper;
import com.cpayusin.member.domain.MemberDomain;
import com.cpayusin.member.infrastructure.Member;
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
    public BoardCreateResponse createBoard(BoardCreateRequest request, MemberDomain memberDomain)
    {
        utilService.isAdmin(memberDomain);
        BoardDomain boardDomain;

        if (request.getParentId() != null) {
            BoardDomain parent = getBoardById(request.getParentId());

            int orderIndex = boardRepository.countChildrenByParentId(parent.getId()) + 1;

            boardDomain = BoardDomain.categoryFrom(request, orderIndex, parent);
        } else {
            int orderIndex = boardRepository.countParent() + 1;
            boardDomain = BoardDomain.boardFrom(request, orderIndex);
        }
        boardDomain = boardRepository.save(boardDomain);
        return BoardCreateResponse.from(boardDomain);
    }


    @Transactional
    @Override
    public void bulkUpdateBoards(List<BoardUpdateRequest> requests, MemberDomain currentMember)
    {
        utilService.isAdmin(currentMember);

        List<Long> removedList = filterRemoveBoard(requests);
        List<BoardUpdateRequest> updateBoardList = filterUpdateBoard(requests);

        List<BoardDomain> boardDomains = updateBoard(updateBoardList);
        boardRepository.saveAll(boardDomains);

        removedList.forEach(this::removeAllChildrenBoard);
        removedList.forEach(this::deleteBoard);
    }

    private List<BoardDomain> updateCategory(BoardDomain parent, List<CategoryUpdateRequest> requests)
    {
        List<BoardDomain> domains = new ArrayList<>();
        for(CategoryUpdateRequest request : requests)
        {
            if (request.getIsDeleted() != null && request.getIsDeleted())
                deleteBoard(request.getId());

            BoardDomain category = getBoardById(request.getId());

            category = category.update(request, parent);
            domains.add(category);
        }
        return domains;
    }

    private List<BoardUpdateRequest> filterUpdateBoard(List<BoardUpdateRequest> requests)
    {
        return requests.stream()
                .filter(board -> board.getIsDeleted() == null || !board.getIsDeleted())
                .toList();
    }

    private List<Long> filterRemoveBoard(List<BoardUpdateRequest> requests)
    {
        return requests.stream()
                .filter(board -> board.getIsDeleted() == null || !board.getIsDeleted())
                .map(BoardUpdateRequest::getId)
                .toList();
    }

    private List<BoardDomain> updateBoard(List<BoardUpdateRequest> requests)
    {
        List<BoardDomain> boardDomains = new ArrayList<>();
        for(BoardUpdateRequest request : requests)
        {
            BoardDomain boardDomain = getBoardById(request.getId());
            boardDomain.update(request);

            if(!request.getCategory().isEmpty()){
                boardDomains.addAll(updateCategory(boardDomain, request.getCategory()));
            }
        }

        return boardDomains;
    }

    @Override
    public BoardDomain getBoardById(Long boardId)
    {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.BOARD_NOT_FOUND));
    }

    @Override
    public BoardResponse findBoardById(Long boardId)
    {
        return BoardResponse.from(getBoardById(boardId));
    }

    @Override
    public List<BoardResponse> findCategoryByBoardId(Long boardId)
    {
        return boardRepository.findBoardByParentBoardId(boardId).stream()
                .map(BoardResponse::from)
                .toList();
    }

    @Override
    public List<BoardMenuResponse> getMenuList()
    {
        List<BoardDomain> result = boardRepository.findAll();
        if (result.isEmpty())
            return Collections.emptyList();

        List<BoardMenuResponse> boardList = result.stream()
                .filter(board -> board.getType().equals(BoardType.BOARD.getCode()))
                .sorted(Comparator.comparingInt(BoardDomain::getOrderIndex))
                .map(BoardMenuResponse::from)
                .toList();


        Map<Long, List<BoardChildrenResponse>> categoryMap = result.stream()
                .filter(b -> b.getType().equals(BoardType.CATEGORY.getCode()))
                .sorted(Comparator.comparingInt(BoardDomain::getOrderIndex))
                .collect(groupingBy(
                        b -> b.getParent().getId(),
                        Collectors.mapping(
                                b -> BoardChildrenResponse.from(b.getParent().getId(), b),
                                toList()
                        )
                ));

        boardList.forEach(b ->
                b.addCategory(categoryMap.getOrDefault(b.getId(), Collections.emptyList())));

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
        List<BoardDomain> childrenList = boardRepository.findBoardByParentBoardId(boardId);
        if (!childrenList.isEmpty())
            childrenList.forEach(childBoard -> {
                deleteBoard(childBoard.getId());
            });
    }

}
