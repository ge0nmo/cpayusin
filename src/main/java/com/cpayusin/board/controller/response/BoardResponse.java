package com.cpayusin.board.controller.response;

import com.cpayusin.board.domain.BoardDomain;
import lombok.Builder;

@Builder
public record BoardResponse(Long id, String name, int orderIndex, Long parentId, boolean isAdminOnly)
{
    public static BoardResponse from(BoardDomain boardDomain)
    {
        return BoardResponse.builder()
                .id(boardDomain.getId())
                .name(boardDomain.getName())
                .orderIndex(boardDomain.getOrderIndex())
                .parentId(boardDomain.getId() != null ? boardDomain.getId() : null)
                .isAdminOnly(boardDomain.getIsAdminOnly())
                .build();
    }

}
