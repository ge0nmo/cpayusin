package com.cpayusin.board.controller.response;

import com.cpayusin.board.domain.BoardDomain;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class BoardMenuResponse
{
    private Long id;

    private String name;

    private String type;

    private Integer orderIndex;
    private Boolean isAdminOnly;

    private List<BoardChildrenResponse> category = new ArrayList<>();

    public static BoardMenuResponse from(BoardDomain boardDomain)
    {
        return BoardMenuResponse.builder()
                .id(boardDomain.getId())
                .name(boardDomain.getName())
                .type(boardDomain.getType())
                .orderIndex(boardDomain.getOrderIndex())
                .isAdminOnly(boardDomain.getIsAdminOnly())
                .build();
    }

    public void addCategory(List<BoardChildrenResponse> category)
    {
        this.category.addAll(category);
    }
}
