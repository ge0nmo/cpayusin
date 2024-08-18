package com.cpayusin.board.controller.response;

import com.cpayusin.board.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardChildrenResponse
{
    private Long id;

    private String name;

    private String type;

    private Integer orderIndex;
    private Boolean isAdminOnly;

    private Long parentId;

    @Builder
    public static BoardChildrenResponse toCategoryResponse(Board board)
    {
        BoardChildrenResponse boardChildrenResponse = new BoardChildrenResponse();
        boardChildrenResponse.id = board.getId();
        boardChildrenResponse.name = board.getName();
        boardChildrenResponse.type = board.getType();
        boardChildrenResponse.orderIndex = board.getOrderIndex();
        boardChildrenResponse.isAdminOnly = board.getIsAdminOnly();
        boardChildrenResponse.parentId = board.getParent().getId();
        return boardChildrenResponse;
    }
}
