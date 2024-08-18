package com.cpayusin.board.controller.response;

import com.cpayusin.board.domain.Board;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardMenuResponse
{
    private Long id;

    private String name;

    private String type;

    private Integer orderIndex;
    private Boolean isAdminOnly;

    @Builder.Default
    private List<BoardChildrenResponse> category = new ArrayList<>();

    public static BoardMenuResponse toBoardResponse(Board board)
    {
        BoardMenuResponse response = new BoardMenuResponse();
        response.id = board.getId();
        response.name = board.getName();
        response.type = board.getType();
        response.orderIndex = board.getOrderIndex();
        response.category = new ArrayList<>();

        return response;
    }
}
