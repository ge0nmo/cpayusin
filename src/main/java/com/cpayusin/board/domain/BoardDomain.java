package com.cpayusin.board.domain;

import com.cpayusin.board.controller.request.BoardCreateRequest;
import com.cpayusin.board.controller.request.BoardUpdateRequest;
import com.cpayusin.board.controller.request.CategoryUpdateRequest;
import com.cpayusin.board.domain.type.BoardType;
import com.cpayusin.board.infrastructure.Board;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardDomain
{
    private Long id;
    private String name;
    private Boolean isAdminOnly;
    private Integer orderIndex;
    private String type;
    private BoardDomain parent;

    public static BoardDomain boardFrom(BoardCreateRequest request, int orderIndex)
    {
        return BoardDomain.builder()
                .name(request.getName())
                .orderIndex(orderIndex)
                .type(BoardType.BOARD.getCode())
                .isAdminOnly(request.getIsAdminOnly())
                .build();
    }

    public static BoardDomain categoryFrom(BoardCreateRequest request, int orderIndex, BoardDomain parent)
    {
        if(parent.getParent() != null){
            throw new BusinessLogicException(ExceptionMessage.BOARD_TYPE_ERROR);
        }

        return BoardDomain.builder()
                .name(request.getName())
                .isAdminOnly(request.getIsAdminOnly())
                .orderIndex(orderIndex)
                .type(BoardType.CATEGORY.getCode())
                .parent(parent)
                .build();
    }

    public BoardDomain update(BoardUpdateRequest request)
    {
        return BoardDomain.builder()
                .id(request.getId())
                .name(request.getName())
                .isAdminOnly(request.getIsAdminOnly())
                .orderIndex(request.getOrderIndex())
                .type(BoardType.BOARD.getCode())
                .build();
    }

    public BoardDomain update(CategoryUpdateRequest request, BoardDomain parent)
    {
        return BoardDomain.builder()
                .id(request.getId())
                .name(request.getName())
                .orderIndex(orderIndex)
                .isAdminOnly(request.getIsAdminOnly())
                .parent(parent)
                .type(BoardType.CATEGORY.getCode())
                .build();
    }


}
