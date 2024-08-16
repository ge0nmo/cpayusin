package com.cpayusin.board.controller.response;

import com.cpayusin.board.domain.BoardDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public record BoardChildrenResponse(Long id, String name, String type, Integer orderIndex, Boolean isAdminOnly,
                                    Long parentId)
{
    public static BoardChildrenResponse from(Long parentId, BoardDomain boardDomain)
    {
        return BoardChildrenResponse.builder()
                .id(boardDomain.getId())
                .name(boardDomain.getName())
                .type(boardDomain.getType())
                .orderIndex(boardDomain.getOrderIndex())
                .isAdminOnly(boardDomain.getIsAdminOnly())
                .parentId(parentId)
                .build();
    }
}
