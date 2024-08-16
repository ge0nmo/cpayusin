package com.cpayusin.board.controller.response;

import com.cpayusin.board.domain.BoardDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
public record BoardCreateResponse(Long id, String name, Integer orderIndex, Long parentId, boolean isAdminOnly)
{
    public static BoardCreateResponse from(BoardDomain boardDomain)
    {
        return BoardCreateResponse.builder()
                .id(boardDomain.getId())
                .name(boardDomain.getName())
                .orderIndex(boardDomain.getOrderIndex())
                .parentId(boardDomain.getParent() != null ? boardDomain.getParent().getId() : null)
                .isAdminOnly(boardDomain.getIsAdminOnly())
                .build();
    }
}
